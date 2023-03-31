package kr.board.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
	
	@PostMapping("memLogin.do")
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
	
}
