package kr.board.mapper;

import org.apache.ibatis.annotations.Mapper;

import kr.board.entity.AuthVO;
import kr.board.entity.Member;

@Mapper
public interface MemberMapper {

	public Member registerCheck(String memID);
	public int register(Member m); // 회원등록 (1성공, 0실패)
	public Member memLogin(Member mvo); // 로그인 체크
	public int memUpdate(Member mvo); // 회원정보수정
	public Member getMember(String memID);
	public void memProfileUpdate(Member mvo);
	public void authInsert(AuthVO authVO);
	public void authDelete(String memID);
}
