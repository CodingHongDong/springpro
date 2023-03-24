package kr.board.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.board.entity.Board;
import kr.board.mapper.BoardMapper;

@Controller
public class BoardController {

	@Autowired
	BoardMapper boardMapper;
	
	@GetMapping("/")
	public String main() {
		return "main";
	}
	
	// @ResponseBody -> jackson-databind(객체를 -> JSON데이터포멧으로 변환)
	@GetMapping("/boardList.do")
	public @ResponseBody List<Board> boardList() {
		List<Board> list = boardMapper.getLists();
		return list; // JSON 데이터 형식으로 변환(api)해서 리턴(응답)하겠다.
	}
	
	@PostMapping("/boardInsert.do")
	public @ResponseBody void boardInsert(Board vo) {
		boardMapper.boardInsert(vo);
	}
	
	@GetMapping("/boardDelete.do")
	public @ResponseBody void boardDelete(@RequestParam("idx") int idx) {
		boardMapper.boardDelete(idx);
	}
	
	@PostMapping("/boardUpdate.do")
	public @ResponseBody void boardUpdate(Board vo) {
		boardMapper.boardUpdate(vo);
	}
	
	@GetMapping("/boardContent.do")
	public @ResponseBody Board boardContent(@RequestParam("idx") int idx) {
		Board vo = boardMapper.boardContent(idx);
		return vo;
	}
	
	@GetMapping("/boardCount.do")
	public @ResponseBody Board boardCount(@RequestParam("idx") int idx) {
		boardMapper.boardCount(idx);
		Board vo = boardMapper.boardContent(idx);
		return vo;
	}
	
}
