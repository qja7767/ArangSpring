package com.medici.arang.user.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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

import com.medici.arang.board.artist.command.ArtworkCommand;
import com.medici.arang.board.artist.command.FindArtistInfoCommand;
import com.medici.arang.board.artist.service.ArtistInfoServiceImpl;
import com.medici.arang.board.artist.service.ArtworkServiceImpl;
import com.medici.arang.board.contact.command.FindContactArtistCommand;
import com.medici.arang.board.contact.command.FindContactCommend;
import com.medici.arang.board.contact.command.FindContactGalleryCommand;
import com.medici.arang.board.contact.command.FindContactingArtistCommand;
import com.medici.arang.board.contact.service.ContactServiceImpl;
import com.medici.arang.board.gallery.command.GalleryCommand;
import com.medici.arang.board.gallery.command.GalleryPageCommand;
import com.medici.arang.board.gallery.service.GalleryInfoServiceImpl;
import com.medici.arang.board.gallery.service.GalleryServiceImpl;
import com.medici.arang.like.domain.LikeVo;
import com.medici.arang.like.service.LikeServiceImpl;
import com.medici.arang.user.command.ArtistCommand;
import com.medici.arang.user.command.ArtistPageCommand;
import com.medici.arang.user.command.GalleristCommend;
import com.medici.arang.user.service.ArtistServiceImpl;
import com.medici.arang.user.service.GalleristServiceImpl;

@Controller("controller.myPageController")
public class MyPageController {
	
	@Autowired
	ArtistServiceImpl artistService;
	@Autowired
	GalleristServiceImpl galleristService;
	@Autowired
	ContactServiceImpl contactService;
	@Autowired
	GalleryServiceImpl galleryService;
	@Autowired
	LikeServiceImpl likeService;
	@Autowired
	ArtworkServiceImpl artworkService;
	@Autowired
	ArtistInfoServiceImpl artistInfoService;
	@Autowired
	GalleryInfoServiceImpl galleryInfoService;
	
	@GetMapping("/mypage/wish_list")
	public String myWishListPage(Model model, HttpServletRequest request) {
		HttpSession session = request.getSession();
		String email = (String)session.getAttribute("email");
		List<String> errorMsgs = new ArrayList<>();
		if(email == null) {
			errorMsgs.add("????????? ????????? ????????????.");
		}if(errorMsgs.size() > 0) {
			model.addAttribute("msg", errorMsgs);
			model.addAttribute("url", "/arang/login");
			return "alert";
		}
		
		//?????? ??????
		List<ArtistPageCommand> artistList = likeService.findArtistJoinLike(email);
		long artistCount = likeService.getLikeArtistCount(email);
		model.addAttribute("artistCount", artistCount);
		model.addAttribute("artistList", artistList);
		
		//?????? ??????
		List<ArtworkCommand> arkworkList = likeService.findArtworkJoinLike(email);
		model.addAttribute("artworkList", arkworkList);
		
		//????????? ??????
		List<GalleryPageCommand> galleryList = likeService.findGalleryJoinLike(email);
		model.addAttribute("galleryList", galleryList);

		
		//????????? ????????? ??????
		List<LikeVo> likeList = likeService.findLike();
		model.addAttribute("likeList", likeList);
		return "mypage/wish_list";
	}
	
	@GetMapping("/mypage/mypage_artist")
	public String artistMypageForm(HttpServletRequest request, Model model) {
		HttpSession session = request.getSession();
		String email = (String)session.getAttribute("email");
		List<String> errorMsgs = new ArrayList<>();
		if(email == null) {
			errorMsgs.add("????????? ????????? ????????????.");
		}if(errorMsgs.size() > 0) {
			model.addAttribute("msg", errorMsgs);
			model.addAttribute("url", "/arang/login");
			return "alert";
		}
		
		ArtistCommand artist = artistService.getArtistByEmail(email);
		request.setAttribute("artist", artist);
		
		List<FindContactGalleryCommand> contactList = 
				contactService.findGalleryByEmail(artist.getAid());
		
		request.setAttribute("contactList", contactList);
		
		
		if(artistInfoService.findArtistInfo(artist.getAid()) != null) {
			request.setAttribute("infoCheck", 1);
		}else {
			request.setAttribute("infoCheck", 0);
		}
		
		// ???????????? ?????? ??????
		String ssn = artist.getSsn();
		String testone = ssn.substring(0, 8);
		System.out.println(testone);
		testone += "******";
		System.out.println(testone);
		request.setAttribute("ssn", testone);
		
		// ????????? ?????? ??????
		String[] careerList = artist.getCareer().split(";");
		request.setAttribute("careerList", careerList);
		
				
		List<ArtworkCommand> artworkList = artworkService.allfindArtwork(artist.getAid());
		model.addAttribute("artworkList", artworkList);
		
		List<FindContactGalleryCommand> contactingList = 
	            contactService.findGalleryContacting(artist.getAid());
	      request.setAttribute("contactingList", contactingList);		
		
		return "mypage/mypage_artist";
	}
	
	
	@GetMapping("/mypage/mypage_request_list2")
	public String requestPageForm2(HttpServletRequest request) {
		HttpSession session = request.getSession();
		String email = (String)session.getAttribute("email");
		ArtistCommand artist = artistService.getArtistByEmail(email);
		
		List<FindContactGalleryCommand> contactList = 
				contactService.findGalleryByEmail(artist.getAid());
		
		request.setAttribute("contactList", contactList);
		return "mypage/mypage_request_list2";
		
	}
	
	

	
	@GetMapping("/mypage/mypage_artist_modify")
	public String mypageUpdateForm(HttpServletRequest request) {
		HttpSession session = request.getSession();
		String email = (String)session.getAttribute("email");
		ArtistCommand artist = artistService.getArtistByEmail(email);
		request.setAttribute("artist", artist);
		
		// ???????????? ????????????
		String ssn = artist.getSsn();
		String testone = ssn.substring(0, 8);
		System.out.println(testone);
		testone += "******";
		System.out.println(testone);
		request.setAttribute("ssn", testone);
		
		// ????????? ?????? ??????
		String[] careerList = artist.getCareer().split(";");
		request.setAttribute("careerList", careerList);
		
		
		return "mypage/mypage_artist_modify";
	}
	
	
	private static final String SAVE_DIR = "C:\\PSH\\my-workSpace\\arang\\src\\main\\webapp\\resources\\img\\";
	private static final String PATH_DIR = "/upload_img/";
	
	
	@PostMapping("/mypage/uploadAjaxAction")
	   public String uploadAjaxPost(@RequestParam("uploadFile") MultipartFile uploadFile, 
	                        HttpServletRequest request) {
	      HttpSession session = request.getSession();
	      String email = (String)session.getAttribute("email");
	      GalleristCommend gallerist = galleristService.findMyGallerist(email);
	      
	      
	      // ???????????? ????????????
	      String ssn = gallerist.getSsn();
	      String testone = ssn.substring(0, 8);
	      System.out.println(testone);
	      testone += "******";
	      System.out.println(testone);
	      request.setAttribute("ssn", testone);
	      
	      
	      String fileRealName = uploadFile.getOriginalFilename(); //???????????? ????????? ??? ?????? ?????????
	      long size = uploadFile.getSize(); //?????? ?????????
	      
	      System.out.println("????????? : "  + fileRealName);
	      System.out.println("????????????(byte) : " + size);
	      
	      String fileExtension = fileRealName.substring(fileRealName.lastIndexOf("."),fileRealName.length());
	      
	      UUID uuid = UUID.randomUUID();
	      System.out.println(uuid.toString());
	      String[] uuids = uuid.toString().split("-");
	      
	      String uniqueName = uuids[0];
	      System.out.println("????????? ???????????????" + uniqueName);
	      System.out.println("????????????" + fileExtension);
	      String forderName = gallerist.getName();
	      
	      String path = SAVE_DIR + "gallerist\\" + forderName; //?????? ??????
	      File Folder = new File(path);
	      
	      System.out.println(path);
	      if (!Folder.exists()) {
	         try{
	             Folder.mkdir(); //?????? ???????????????. ("?????????"??? ??????)
	             System.out.println("????????? ????????????.");
	              } 
	              catch(Exception e){
	             e.getStackTrace();
	         }        
	            }else {
	         System.out.println("????????? ?????? ???????????????..");
	      }
	      
	      File saveFile = new File(SAVE_DIR + "gallerist/" + forderName + "/" + uniqueName + fileExtension);  // ?????? ???
	      try {
	         uploadFile.transferTo(saveFile); // ?????? ?????? ???????????????
	      } catch (IllegalStateException e) {
	         e.printStackTrace();
	      } catch (IOException e) {
	         e.printStackTrace();
	      }
	      
	      gallerist.setImgPath(PATH_DIR + "gallerist/" + forderName + "/" + uniqueName+fileExtension);
	      galleristService.updateGalleristByEmail(gallerist);
	      request.setAttribute("gallerist", gallerist);
	      
	      return "/mypage/mypage_gallerist_modify";
	   }
	
	
	@PostMapping("/mypage/mypage_gallerist_modify")
	   public String galleristmypageUpdate(GalleristCommend inputGallerist,
	                        HttpServletRequest request, Model model) {
	      model.addAttribute("gallerist", inputGallerist);
	      String ssn = request.getParameter("ssn");
	      inputGallerist.setSsn(ssn);
	      model.addAttribute("ssn", ssn);
	      
	      galleristService.updateGalleristByEmail(inputGallerist);

	      return "mypage/mypage_gallerist";
	   }
	
	
	@GetMapping("/mypage/mypage_gallerist")
	public String galleristMypageForm(HttpServletRequest request, Model model) {
		HttpSession session = request.getSession();
		String email = (String)session.getAttribute("email");		
		GalleristCommend gallerist = galleristService.findMyGallerist(email);
		request.setAttribute("gallerist", gallerist);
		List<String> errorMsgs = new ArrayList<>();
		if(email == null) {
			errorMsgs.add("????????? ????????? ????????????.");
		}if(errorMsgs.size() > 0) {
			model.addAttribute("msg", errorMsgs);
			model.addAttribute("url", "/arang/login");
			return "alert";
		}
		
		
		// ???????????? ????????????
		String ssn = gallerist.getSsn();
		String testone = ssn.substring(0, 8);
		System.out.println(testone);
		testone += "******";
		System.out.println(testone);
		request.setAttribute("ssn", testone);
		
		List<GalleryPageCommand> myGalleryList = 
				galleryService.findMyGallery(email);
		request.setAttribute("myGalleryList", myGalleryList);
		long galleryCount = galleryService.getGalleryCount();
		request.setAttribute("galleryCount", galleryCount);
		
		List<FindContactingArtistCommand> contactingList = 
				contactService.findArtistList(email);
		request.setAttribute("contactingList", contactingList);
		
		List<FindContactArtistCommand> artistList = 
				contactService.findArtistkList(email);
		request.setAttribute("artistList", artistList);
		
		return "mypage/mypage_gallerist";		
	}
	
	
	@GetMapping("/mypage/mypage_gallerist_modify")
	public String galleristmypageUpdateForm(HttpServletRequest request) {
		HttpSession session = request.getSession();
		String email = (String)session.getAttribute("email");
		GalleristCommend gallerist = galleristService.findMyGallerist(email);
		request.setAttribute("gallerist", gallerist);
		
		// ???????????? ????????????
		String ssn = gallerist.getSsn();
		String testone = ssn.substring(0, 8);
		System.out.println(testone);
		testone += "******";
		System.out.println(testone);
		request.setAttribute("ssn", testone);
		
		return "mypage/mypage_gallerist_modify";
	}

	// contact ??????
	
	@GetMapping("/mypage/mypage_request_list")
	public String requestPageForm(HttpServletRequest request) {
		HttpSession session = request.getSession();
		String email = (String)session.getAttribute("email");
		GalleryCommand gallery = galleryService.findGalleryByEmail(email);
		
		List<FindContactCommend> contactList = 
				contactService.findArtistkById(gallery.getCode());
		
		request.setAttribute("contactList", contactList);
		return "mypage/mypage_request_list";
		
	}
	
	
	//????????? ??????
	@PostMapping("/mypage/yesga")
	   public String acceptBtnga(HttpServletRequest request) {
	      String yesBtn = request.getParameter("yesBtn");
	      long id = Long.parseLong(request.getParameter("contactId"));
	      System.out.println(yesBtn);
	      
	      contactService.acceptTpye(yesBtn, id);
	      
	      return "redirect:/mypage/mypage_artist";
	   }
	   
	@PostMapping("/mypage/nodga")
	  public String notBtnga(HttpServletRequest request) {
	     String noBtn = request.getParameter("noBtn");
	     long id = Long.parseLong(request.getParameter("contactId"));
	     System.out.println(noBtn);
	     
	     contactService.acceptTpye(noBtn, id);
	     
	     return "redirect:/mypage/mypage_artist";
	  }
	
	@PostMapping("/mypage/yesda")
	  public String acceptBtnda(HttpServletRequest request) {
	    String yesBtn = request.getParameter("yesBtn");
	    System.out.println(yesBtn);
	    long id = Long.parseLong(request.getParameter("contactId"));
	    System.out.println(id);
	    contactService.acceptTpye(yesBtn, id);
	     
	    return "redirect:/mypage/mypage_gallerist";
	    }
	   
	@PostMapping("/mypage/noda")
	  public String notBtn(HttpServletRequest request) {
	     String noBtn = request.getParameter("noBtn");
	     long id = Long.parseLong(request.getParameter("contactId"));
	     System.out.println(noBtn);
	     System.out.println(id);
	     contactService.acceptTpye(noBtn, id);
	      
	     return "redirect:/mypage/mypage_gallerist";
	   }
	
	
}
