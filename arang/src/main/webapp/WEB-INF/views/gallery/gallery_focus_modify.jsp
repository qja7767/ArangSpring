<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
  <title>갤러리상세페이지</title>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, minimum-scale=1, user-scalable=no">
  <link rel="stylesheet" type="text/css" href="/fake_resources/css/default/normalize.css"/>
  <link rel="stylesheet" type="text/css" href="/fake_resources/css/default/default.css"/>
  <link rel="stylesheet" type="text/css" href="/fake_resources/css/artist/artist_focus.css">
  <link rel="stylesheet" type="text/css" href="/fake_resources/css/gallery/gallery_focus.css">
  <script type="text/javascript" src="/fake_resources/js/jquery.js"></script>
</head>

<body>
  <div id="wrap">
    
    <jsp:include page="/WEB-INF/views/header/header_main.jsp"/>
    
    <div class="headline">
      <h3>Gallery</h3>
      <nav id="sub-nav">
        <ul>
          <li><a href="../html_gallery/gallery.html">All</a></li>
          <li><a href="../html_gallery/Seoul.html">Seoul</a></li>
          <li><a href="../html_gallery/Gyeonggi.html">Gyeonggi</a></li>
          <li><a href="../html_gallery/Deajeon.html">Deajeon</a></li>
          <li><a href="../html_gallery/Daegu.html">Daegu</a></li>
          <li><a href="../html_gallery/Busan.html">Busan</a></li>
          <li><a href="../html_gallery/Jeju.html">Jeju</a></li>
          <li><a href="../html_gallery/Other.html">Other</a></li>
        </ul>
      </nav>
    </div>

    <!-- 갤러리상세페이지 -->
    <form action="gallery_upload" method="post" enctype="multipart/form-data">
    </form>
    <div id="artist">
      <div id="artist_detail">
        <div class="container">
          <div class="detail_wrapper d-flex">
            <div class="side_block">
              <div class="artist_info"> 
                <h3><input type="text" name="galleryName_kor" value="${galleryCommand.galleryName_kor}"></h3>
                <p class="e_name"><input type="text" name="galleryName_eng" value="${galleryCommand.galleryName_eng}"></p>
                <p class="born"><input type="text" name="since" value="${galleryCommand.since}"></p>
                <div class="artist_avatar">
                  <form action="/gallery/gallery_focus_modify" method="post" enctype="multipart/form-data">
                        <img id="changeImg" src="${galleryCommand.galleryImgPath}" class="click">
                        <input id="uploadFile" type="file" name="uploadFile" style="display: none;" onchange="imgchange(this)">
                        <button id="uploadBtn" style="display: none;"></button>
                      </form>
                </div>
              </div>
            </div>
            <div class="content_block">
              <div class="review">
                <h2><input type="text" name="galleryName_eng" value="${galleryCommand.galleryName_eng}" style="height: 40px;"></h2>
                <p><textarea id="editor" class="textarea_box" placeholder="갤러리 소개" cols="43" rows="7" name="description">${galleryCommand.description}</textarea></p>
                <p><br></p>
                <p>
                <c:forTokens var="infoImg" items="${galleryCommand.infoImgPath}" delims=";">
            		<img src="${infoImg}">
            	</c:forTokens>
                </p>
              </div>
              <div class="product">
                <h4 class="sub_title">Gallery Infomation</h4>
                <div class="artwork_simul">
                  <div class="artwork_wrapper">
                    <div class="artwork_info_wrap">
                      <div class="artwork_info">
                        <div class="info">
                          <div class="info_block">
                            <div class="first">주소(Address)</div>
                            <div class="last">
                              <input type="text" name="address" value="${galleryCommand.address}" style="width: 300px; height: 40px;">
                            </div>
                          </div>
                          <div class="info_block">
                            <div class="first" style="">크기(Size)</div>
                            <div class="last">
                              <input type="text" name="area" value="${galleryCommand.area}" style="width: 300px; height: 40px;">
                            </div>
                          </div>
                          <div class="info_block">
                            <div class="first">시간(Hours)</div>
                            <div class="last">
                              <input type="text" name="openClose" value="${galleryCommand.openClose}" style="width: 300px; height: 40px;">
                            </div>
                          </div>
                          <div class="info_block">
                            <div class="first">이메일(Email)</div>
                            <div class="last"><input type="text" name="galleryEmail" value="${galleryCommand.galleryEmail}" style="width: 300px; height: 40px;"></div>
                          </div>
                          <div class="info_block">
                            <div class="first">전화번호(Phone)</div>
                            <div class="last"><input type="text" name="galleryPhone" value="${galleryCommand.galleryPhone}" style="width: 300px; height: 40px;"></div>
                          </div>
                        </div>
                      </div>
                      <div class="button_wrap" style="position: relative;">
                        <div class="btn_group">
                        <form action="gallery_focus" method="post">
                          <a href="#"><button class="btn1" type="submit">수정완료</button></a>
                          <input type="hidden" name="galleryCode" value="${galleryCommand.code}" />
                        </form>
                          <a href="#"><button class="btn1" type="submit">삭제</button></a>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div class="side_block">
              <div class="a_list">
                <h3>추천작가</h3>
                <ul>
                  <li>
                    <a href="#">신수희</a>
                  </li>
                  <li>
                    <a href="#">최선호</a>
                  </li>
                </ul>
              </div>
              <div class="a_banner">
                <h3>이달의 전시</h3>
                <a href="#"><img class="exhibition_banner" src="/upload_img/banner/김선배너.jpg" alt="exhibition_banner"></a>
              </div>
            </div>
          </div>
        </div>
      </div>

    </div>
  </div>
  <script src="https://cdn.ckeditor.com/ckeditor5/35.0.1/classic/ckeditor.js"></script>
  <script>
  $('#uploadBtn').click(function(event) {
	   console.log(this.innerText);
	   let clickCategory = this.innerText;
	   $.ajax({
	       type:"post",
	       url : '/gallery/gallery_focus_modify',
	       contentType: false,
	       dataType: "file",
	       data : JSON.stringify(
	             {
	             <!-- 보내지는 데이터 영역 -->
	             "categoryValue" : clickCategory
	             }
	             ),
	       success: function(data){
	          if(data == "error"){
	                alert("데이터 전송 실패!!");
	          }else{                 
	             console.log("데이터 전송 성공!!");
	             console.log(data);
	             alert(data);
	             }
	          }
	   })
	});

	let click = document.querySelector(".click");
	let clickTarget = document.querySelector("#uploadFile");
	let btnTarget = document.querySelector("#uploadBtn");
	click.addEventListener("click", function() {
	   alert("이미지를 선택해주세요.");
	   clickTarget.click();
	});

	function imgchange(e) {
	   btnTarget.click();
	}
	
	cnt = 0;
	const add_textbox = () => {
		if ( cnt < 2 ) {
			const box = document.getElementById("box2");
			const newP = document.createElement('tr');
			newP.innerHTML = "<div class='upload_box'> <input type='file' id='input-file' name='imgName2' multiple>"
			+ "</div> <br> <input type='button' value='삭제' onclick='remove(this)'>";
			box.appendChild(newP);
			cnt++;
		}
	}
	const remove = (obj) => {
	    document.getElementById('box2').removeChild(obj.parentNode);
	    cnt--;
	}
	ClassicEditor.create( document.querySelector( '#editor' ) );
	
	
  </script>
</body>

</html>
