package kr.board.controller;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

import kr.board.entity.Member;
import kr.board.mapper.MemberMapper;

@Controller
public class MemberController {

	@Autowired
	private MemberMapper memberMapper;
	
	@GetMapping("/memJoin.do")
	public String memJoin() {
		return "member/join";
	}
	
	// 회원가입 처리
	@PostMapping("/memRegister.do")
	public String memRegister(Member m, String memPassword1, String memPassword2, RedirectAttributes rttr, HttpSession session ) {
		if(m.getMemID() == null || m.getMemID().equals("") ||
			memPassword1 == null || memPassword1.equals("") ||
			memPassword2 == null || memPassword2.equals("") ||
		   m.getMemName() == null || m.getMemName().equals("") ||
		   m.getMemAge() == 0 || 
		   m.getMemGender() == null || m.getMemGender().equals("") ||
		   m.getMemEmail() == null || m.getMemEmail().equals("")) {
		    // 누락 메세지를 가지고 가기? => 객체바인딩(Model, HttpServletRequest, HttpSession)
			rttr.addFlashAttribute("msgType", "실패 메세지");
			rttr.addFlashAttribute("msg", "모든 내용을 입력하세요.");
			return "redirect:/memJoin.do";	
		} 
		if(!memPassword1.equals(memPassword2)) {
			rttr.addFlashAttribute("msgType", "실패 메세지");
			rttr.addFlashAttribute("msg", "비밀번호가 서로 다릅니다.");
			return "redirect:/memJoin.do";
		}
		
		m.setMemProfile(""); // 사진이미지는 없다는 의미
		int result = memberMapper.register(m);
		if(result == 1) {
			rttr.addFlashAttribute("msgType", "성공 메세지");
			rttr.addFlashAttribute("msg", "회원가입 성공 !");
			// 회원가입이 성공하면 => 로그인처리하기
			session.setAttribute("mvo", m);
			return "redirect:/";
		} else {
			rttr.addFlashAttribute("msgType", "실패 메세지");
			rttr.addFlashAttribute("msg", "이미 존재하는 회원입니다.");
			return "redirect:/memJoin.do";
		}
	}
	
	@GetMapping("/memRegisterCheck.do")
	public @ResponseBody int memRegisterCheck(@RequestParam("memID") String memID) {
		Member m = memberMapper.registerCheck(memID);
		if(m != null || memID.equals("")) {
			return 0;
		} 
		return 1;
	}
	
	@GetMapping("/memLogout.do")
	public String memLogout(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}
	
	@GetMapping("/memLoginForm.do")
	public String memLoginForm() {
		
		return "member/memLoginForm";
	}
	
	@PostMapping("/memLogin.do")
	public String memLogin(HttpSession session, Member m, RedirectAttributes rttr) {
		if(m.getMemID() == null || m.getMemID().equals("") ||
		   m.getMemPassword() == null || m.getMemPassword().equals("")	) {
			rttr.addFlashAttribute("msgType", "실패 메세지");
			rttr.addFlashAttribute("msg", "모든 내용을 입력해주세요.");
			return "redirect:/memLoginForm.do";
		}
		Member mvo = memberMapper.memLogin(m);
		if(mvo != null) {
			rttr.addFlashAttribute("msgType", "성공 메세지");
			rttr.addFlashAttribute("msg", "로그인 성공 !");
			session.setAttribute("mvo", mvo);
			return "redirect:/";
		} else {
			rttr.addFlashAttribute("msgType", "실패 메세지");
			rttr.addFlashAttribute("msg", "다시 로그인 해주세요 !");
			return "redirect:/memLoginForm.do";
		}
	}
	
	@GetMapping("/memUpdateForm.do")
	public String memUpdateForm() {
		return "member/memUpdateForm";
	}
	
	@PostMapping("/memUpdate.do")
	public String memUpdate(Member m, RedirectAttributes rttr, String memPassword1, String memPassword2, HttpSession session) {
		if(m.getMemID() == null || m.getMemID().equals("") ||
		   memPassword1 == null || memPassword1.equals("") ||
	 	   memPassword2 == null || memPassword2.equals("") ||
		   m.getMemName() == null || m.getMemName().equals("") ||
		   m.getMemAge() == 0 || 
		   m.getMemGender() == null || m.getMemGender().equals("") ||
		   m.getMemEmail() == null || m.getMemEmail().equals("")) {
			    // 누락 메세지를 가지고 가기? => 객체바인딩(Model, HttpServletRequest, HttpSession)
			rttr.addFlashAttribute("msgType", "실패 메세지");
			rttr.addFlashAttribute("msg", "모든 내용을 입력하세요.");
			return "redirect:/memUpdateForm.do";	
		} 
		if(!memPassword1.equals(memPassword2)) {
			rttr.addFlashAttribute("msgType", "실패 메세지");
			rttr.addFlashAttribute("msg", "비밀번호가 서로 다릅니다.");
			return "redirect:/memUpdateForm.do";
		}
		int result = memberMapper.memUpdate(m);
		if(result == 1) { // 수정성공 메세지
			rttr.addFlashAttribute("msgType", "성공 메세지");
			rttr.addFlashAttribute("msg", "회원정보 수정 성공 !");
			Member mvo = memberMapper.getMember(m.getMemID());
			session.setAttribute("mvo", mvo);
			return "redirect:/";
		} else {
			rttr.addFlashAttribute("msgType", "실패 메세지");
			rttr.addFlashAttribute("msg", "회원정보 수정 실패!!!");
			return "redirect:/memUpdateForm.do";
		}	
	}
	
	@GetMapping("/memImageForm.do")
	public String memImageForm() {
		return "member/memImageForm";
	}
	
	// 회원사진 이미지 업로드(upload, DB)
	@PostMapping("/memImageUpdate.do")
	public String memImageUpdate(HttpServletRequest request, HttpSession session,RedirectAttributes rttr) {
		// 파일 업로드 API 필요
		MultipartRequest multi = null;
		int fileMaxSize = 10*1024*1024; // 10MB
		// 실제로 upload할 path
		String savePath = request.getRealPath("resources/upload");
		try {
			// 이미지 업로드
			multi = new MultipartRequest(request, savePath, fileMaxSize, "UTF-8", new DefaultFileRenamePolicy());
		} catch (IOException e) {
			e.printStackTrace();
			rttr.addFlashAttribute("msgType", "실패 메세지");
			rttr.addFlashAttribute("msg", "파일의 크기는 10MB를 넘을 수 없습니다.");
			return "redirect:/memImageForm.do";
		}
		// DB 테이블에 회원이미지 업데이트
		String memID = multi.getParameter("memID");
		String newProfile = "";
		File file = multi.getFile("memProfile");
		if(file != null) {
			String ext = file.getName().substring(file.getName().lastIndexOf(".") + 1);
			ext = ext.toUpperCase();
			if(ext.equals("PNG") || ext.equals("GIF") || ext.equals("JPG") || ext.equals("JPEG")) {
				// 새로 업로도된 이미지(new), 현재 DB에 있는 이미지(old)
				String oldProfile = memberMapper.getMember(memID).getMemProfile();
				File oldFile = new File(savePath + "/" + oldProfile);
				if(oldFile.exists()) {
					oldFile.delete();
				}
				newProfile = file.getName();
			} else {
				if(file.exists()) {
					file.delete();
				}
				rttr.addFlashAttribute("msgType", "실패 메세지");
				rttr.addFlashAttribute("msg", "이미지 파일만 업로드 가능하다잉");
				return "redirect:/memImageForm.do";
			}
		}
		// 새로운 이미지 DB 저장
		Member mvo = new Member();
		mvo.setMemID(memID);
		mvo.setMemProfile(newProfile);
		memberMapper.memProfileUpdate(mvo); // 이미지 업데이트 성공
		Member m = memberMapper.getMember(memID);
		// 세션을 새롭게 생성한다.
		session.setAttribute("mvo", m);
		rttr.addFlashAttribute("msgType", "성공 메세지");
		rttr.addFlashAttribute("msg", "회원 사진 등록 성공 !");
		return "redirect:/";
	}
	
	
}
