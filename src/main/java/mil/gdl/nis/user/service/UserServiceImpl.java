package mil.gdl.nis.user.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mil.gdl.nis.auth.service.AuthService;
import mil.gdl.nis.cmmn.PwFailCacheData;
import mil.gdl.nis.cmmn.service.CacheService;
import mil.gdl.nis.cmmn.util.Uid;
import mil.gdl.nis.dao.DAO;
import mil.gdl.nis.user.vo.UserVo;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService,UserDetailsService {
	
	private final DAO dao;
	private final MessageSourceAccessor messageSource;
	private final CacheService cacheService;
	private final AuthService authService;
	
	@Value("${user.pwFailKey}")
	private String pwFailKey;
	@Value("${user.pwFaileCnt}")
	private int pwFailCnt;
	@Value("${user.pwFaileLockMin}")
	private int pwFaileLockMin;

	/**
	 * Spring Security 필수 메소드 구현
     * @param username 
     * @return UserDetails
     * @throws UsernameNotFoundException 유저가 없을 때 예외 발생
	 */
	//@SuppressWarnings("unchecked")
	@Override
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException,AuthenticationServiceException{
		UserVo user = (UserVo)dao.selectOne("User.selectUser", userId);
		if(user == null) {
			throw new UsernameNotFoundException(messageSource.getMessage("error.userNotFound"));
		}
		
		Map<String, PwFailCacheData> pwdFailCacheData = cacheService.getPwdFailCacheData(pwFailKey);
		int failCnt = pwdFailCacheData.containsKey(user.getUserId()) ? pwdFailCacheData.get(user.getUserId()).getPwFailCnt() : 0;
        if (failCnt >= pwFailCnt) {
			if(!LocalDateTime.now().isAfter(pwdFailCacheData.get(user.getUserId()).getPwFailDateTime().plusMinutes(pwFaileLockMin))) {
				throw new AuthenticationServiceException(messageSource.getMessage("error.userLockedByError",new String[] { String.valueOf(pwFaileLockMin) }));
			}
		}

		user.setUserPw(user.getUserPw().replace("&#36;", "$"));
		//List<String> roles = (List<String>)dao.selectList("User.selectUserRole", userId);
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(); 
		authorities.add(new SimpleGrantedAuthority(user.getAuthority().name())); 
		/*
		for (String authority : roles) 
		{ 
			authorities.add(new SimpleGrantedAuthority(authority)); 
			user.setAuthority(Authority.valueOf(authority));
		} 
		*/
		user.setAuthorities(authorities);
		return user;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<UserVo> getLoginUser(Map<String, Object> map) throws Exception {
		List<UserVo> userList = (List<UserVo>)dao.selectList("User.selectLoginUser", map);
		for(UserVo vo:userList) {
			vo.setUserPw(vo.getUserPw().replace("&#36;", "$"));
			List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(); 
			vo.setAuthorities(authorities);
		}
		return userList;
	}
	
	@Override
	public UserVo getUser(UserVo user) throws Exception {
		UserVo rtnUser = (UserVo)dao.selectOne("User.selectUser", user.getUserId());
		rtnUser.setUserPw(rtnUser.getUserPw().replace("&#36;", "$"));
		return rtnUser;
	}

	

	@Override
	public int register(UserVo user) throws Exception{
	
		user.setId(Uid.makeLongUUID());
		user.setUserPw(user.getUserPw().replaceAll("\\$|\\>", "&#36;"));
		authService.insertEquip(user);
		return dao.update("User.mergeUser", user);
		//user.setId(Uid.makeLongUUID());
		//rtn =+ dao.update("User.mergeUserRole", user);
		//return rtn;
	}




	@Override
	public int updatePwFailCnt(String userId) throws Exception{
		return dao.update("User.updatePwFailCnt", userId);
	}




	@Override
	public int modify(UserVo user) throws Exception{
		user.setUserPw(user.getUserPw().replaceAll("\\$|\\>", "&#36;"));
		return dao.update("User.mergeUser", user);
		//rtn =+ dao.update("User.mergeUserRole", user);
		//return rtn;
	}




	@Override
	public int changePwd(UserVo user) throws Exception{
		user.setUserPw(user.getUserPw().replaceAll("\\$|\\>", "&#36;"));
		return dao.update("User.updateUserPw", user);
	}
	
	@Override
	public int initPwd(UserVo user) throws Exception{
		user.setUserPw(user.getUserPw().replaceAll("\\$|\\>", "&#36;"));
		return dao.update("User.initUserPw", user);
	}
	
	
	
	@Override
	public int updateLoginData(UserVo user) throws Exception{
		return dao.update("User.updateLoginData", user);
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getUserList(Map<String, Object> map) throws Exception {
		return (List<Map<String,Object>>)dao.selectPage("User.selectUserList", map);
	}




	@Override
	public int checkUserId(String userId) throws Exception {
		return dao.selectCount("User.selectUserId", userId);
	}




	@Override
	public int deleteUser(Map<String, Object> map) throws Exception {
		int rtn = 0;
		rtn += dao.delete("Auth.deleteEquip", map);
		rtn += dao.delete("User.deleteUserRole", map);
		rtn += dao.delete("User.deleteUser", map);
		return rtn;
	}

	

}
