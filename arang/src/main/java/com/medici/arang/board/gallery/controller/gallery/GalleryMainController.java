package com.medici.arang.board.gallery.controller.gallery;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.medici.arang.board.contact.command.ContactCommand;
import com.medici.arang.board.contact.service.ContactServiceImpl;
import com.medici.arang.board.gallery.command.GalleryCommand;
import com.medici.arang.board.gallery.command.GalleryInfoCommand;
import com.medici.arang.board.gallery.command.GalleryPageCommand;
import com.medici.arang.board.gallery.service.GalleryInfoServiceImpl;
import com.medici.arang.board.gallery.service.GalleryServiceImpl;
import com.medici.arang.like.domain.LikeVo;
import com.medici.arang.like.service.LikeServiceImpl;
import com.medici.arang.user.command.ArtistCommand;
import com.medici.arang.user.command.ArtistPageCommand;
import com.medici.arang.user.service.ArtistServiceImpl;

@Controller
public class GalleryMainController {
	
	@Autowired
	GalleryInfoServiceImpl galleryInfoService;
	
	@Autowired
	GalleryServiceImpl galleryService;
	
	@Autowired
	ArtistServiceImpl artistService;
	
	@Autowired
	ContactServiceImpl contactService;
	
	@Autowired
	LikeServiceImpl likeService;
	
	@GetMapping("/gallery/gallery")
	public String Gallery(Model model, HttpServletRequest request) {
		List<GalleryPageCommand> galleryList = galleryInfoService.allFindGallery();
		
		for (GalleryPageCommand galleryCommand : galleryList) {
			String a = galleryCommand.getInfoImgPath();
			String[] b =  a.split(";");
			String c = b[0];
			galleryCommand.setInfoImgPath(c);
		}
		
		long galleryCount = galleryService.getGalleryCount();
		
		model.addAttribute("galleryCount", galleryCount);
		model.addAttribute("galleryList", galleryList);
		
		// ?????????
		int page = 0;
		if(request.getParameter("page") != null) {
			page = Integer.parseInt(request.getParameter("page"));			
		}
		//?????????
		Pageable pageable = PageRequest.of(page, 6, Sort.Direction.DESC, "code");
		Page<GalleryPageCommand> galleryPagingList = 
				galleryInfoService.allFindGalleryPage(pageable);
		
		//???????????????
		int pageNumber = galleryPagingList.getPageable().getPageNumber();
		//??? ????????????
		int totalPages = galleryPagingList.getTotalPages();
		//????????? ???
		int pageBlock = 5;
		//?????? ???????????? 7????????? 1*5+1=6
		int startBlockPage = ((pageNumber)/pageBlock)*pageBlock+1;
		//6+5-1=10. 6,7,8,9,10?????? 10.
		int endBlockPage = startBlockPage+pageBlock-1;
		endBlockPage= totalPages<endBlockPage? totalPages:endBlockPage;
		
		for (GalleryPageCommand galleryCommand : galleryPagingList) {
			String a = galleryCommand.getInfoImgPath();
			String[] b =  a.split(";");
			String c = b[0];
			galleryCommand.setInfoImgPath(c);
		}
		
		
		
		model.addAttribute("startBlockPage", startBlockPage);
		model.addAttribute("endBlockPage", endBlockPage);
		model.addAttribute("galleryPagingList", galleryPagingList);
		
		return "gallery/gallery";
	}
	
	@GetMapping("/gallery/gallery_focus")
	public String GalleryInfoForm(@RequestParam("code") long code, Model model,
			HttpServletRequest request) {
		
		GalleryPageCommand galleryCommand = galleryInfoService.findGalleryByID(code);
		HttpSession session = request.getSession();
		String email = (String)session.getAttribute("email");
		
		model.addAttribute("galleryCommand", galleryCommand);
		
		
		///?????????id code??? ??????
		LikeVo findLike = likeService.findLike(email, code);
		

		if(findLike != null) {
			model.addAttribute("likeNum", 4);
		}else {
			model.addAttribute("likeNum", 5);		
		}
		
		return "gallery/gallery_focus";
	}
	
	@PostMapping("/gallery/gallery_focus")
	public String ContactGallery(Model model, HttpServletRequest request) {
		long code = Integer.parseInt(request.getParameter("galleryCode"));
		System.out.println(code);
		GalleryPageCommand galleryCommand = galleryInfoService.findGalleryByID(code);
		model.addAttribute("galleryCommand", galleryCommand);
		
		HttpSession session = request.getSession();
		String artistEmail = (String) session.getAttribute("email");
		System.out.println(artistEmail);
		ArtistCommand artist = artistService.getArtistByEmail(artistEmail);
		
		ContactCommand contactCommand = new ContactCommand();
		System.out.println("?????? aid"+artist.getAid());
		
		contactCommand.setArtistId(artist.getAid());
		contactCommand.setGalleryCode(code);
		contactCommand.setSendingType("A");
		System.out.println(contactCommand.getGalleryCode());
		System.out.println("????????? aid"+contactCommand.getArtistId());
		contactService.contactGallery(contactCommand);
		
		
		return "gallery/gallery_focus";
	}
	
	/**
	 * ????????? ?????? ???????????? ?????? + ????????? ????????? ????????????
	 */
	@GetMapping("/gallery/gallery_focus_modify")
	public String GalleryInfoFormModify(@RequestParam("code") long code, Model model,
			HttpServletRequest request) {
		
		GalleryPageCommand galleryCommand = galleryInfoService.findGalleryByID(code);
		HttpSession session = request.getSession();
		String email = (String)session.getAttribute("email");
		
		model.addAttribute("galleryCommand", galleryCommand);
		
		return "gallery/gallery_focus_modify";
	}
	
	//????????? ????????? ??????
	private static final String SAVE_DIR = "C:\\PSH\\my-workSpace\\arang\\src\\main\\webapp\\resources\\img\\";
	private static final String PATH_DIR = "/upload_img/";
	
	@PostMapping("/gallery/gallery_focus_modify")
	public String insertGalleryForm(GalleryCommand galleryCommand, GalleryInfoCommand infoCommand,
					@RequestParam("uploadFile") List<MultipartFile> multiFileList,
					HttpServletRequest request, Model model) throws Exception {
		HttpSession session = request.getSession();
		String galleristEmail = (String) session.getAttribute("email");
		System.out.println(galleristEmail);
		galleryCommand.setGalleristEmail(galleristEmail);
		
		
		File fileCheck = new File(SAVE_DIR);
		//?????? ????????? ????????? ???????????????
		if(!fileCheck.exists()) fileCheck.mkdirs();
		
		List<Map<String, String>> fileList = new ArrayList<>();
		List<String> imgList = new ArrayList<String>();
		
		for(int i = 0; i < multiFileList.size(); i++) {
			//?????? ?????? ??????
			String originFile = multiFileList.get(i).getOriginalFilename();
			//?????? ?????? ?????????
			String ext = originFile.substring(originFile.lastIndexOf("."));
			//UUID ?????? ?????? + ?????????
			UUID uuid = UUID.randomUUID();
			String[] uuids = uuid.toString().split("-");			
			String changeFile = uuids[0] + ext;
			imgList.add(changeFile);
			
			Map<String, String> map = new HashMap<>();
			map.put("originFile", originFile);
			map.put("changeFile", changeFile);
			fileList.add(map);
		}
		System.out.println(imgList);
		String galleryImg = imgList.get(0);
		imgList.remove(0);
		/**
		 * ????????? ???????????? ????????? ????????? ???????????? ????????? ?????? ?????????.
		 * ????????? ?????? ; ??? ?????????
		 */
		
		String imgPathList = "";		
		for (String string : imgList) {
			imgPathList += PATH_DIR;
			imgPathList += "gallery\\";
			imgPathList += string;
			imgPathList += ";";
		}
		System.out.println(imgPathList);
		
		//?????? ?????? ?????? ?????? ???
		try {
			for(int i = 0; i < multiFileList.size(); i++) {
				File uploadFile = new File(SAVE_DIR + "gallery\\" + fileList.get(i).get("changeFile"));
				multiFileList.get(i).transferTo(uploadFile);
			}
		} catch (IllegalStateException | IOException e) {
			// ????????? ????????? ?????? ??????
			for(int i = 0; i < multiFileList.size(); i++) {
				new File(SAVE_DIR + "gallery\\" + fileList.get(i).get("changeFile")).delete();
			}
			e.printStackTrace();
		}
		
		/**
		 * DB ?????? ?????????
		 * ????????? ????????? ?????? ??? ???????????? ?????? ?????? ?????? ????????? ????????? ????????? DB??????
		 */
		galleryCommand.setGalleryImgPath(PATH_DIR + "\\gallery\\" + galleryImg);
		infoCommand.setInfoImgPath(imgPathList);
		String imgName = galleryCommand.getGalleryImgPath();
		System.out.println(imgName);
		
//		galleryService.insertGallery(galleryCommand);
//		galleryInfoService.insertGalleryInfo(infoCommand);
		String representerNum = galleryCommand.getRepresenterNum();
		GalleryCommand a = galleryService.findAllGalleryByRepresenterNum(representerNum);
		infoCommand.setGalleryCode(a.getCode());
		
		return "/gallery/gallery_focus_modify?code="+a.getCode();
	}
}
