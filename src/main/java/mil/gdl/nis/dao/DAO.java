package mil.gdl.nis.dao;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import mil.gdl.nis.cmmn.vo.PageInfo;

/**
 * @Package : mil.gdl.dis.cmmn.dao
 * @FileName : DAO.java
 * @author : kdh
 * @Date : 2022. 4. 10.
 * @Description :
 */
@RequiredArgsConstructor
@Repository("dao")
public class DAO {

	private final SqlSessionTemplate sqlSession;

	/**
	 * Insert.
	 *
	 * @param queryId the query id
	 * @param params  the params
	 * @return the int
	 */
	public int insert(String queryId, Object params) {
		return sqlSession.insert(queryId, params);
	
	}

	/**
	 * Update.
	 *
	 * @param queryId the query id
	 * @param params  the params
	 * @return the int
	 */
	public int update(String queryId, Object params) {
		return sqlSession.update(queryId, params);
	}

	/**
	 * Delete.
	 *
	 * @param queryId the query id
	 * @param params  the params
	 * @return the int
	 */
	public int delete(String queryId, Object params) {
		return sqlSession.delete(queryId, params);

	}

	public int delete(String queryId) {
		return sqlSession.delete(queryId);
	}

	/**
	 * Select one.
	 *
	 * @param queryId the query id
	 * @return the object
	 */
	public Object selectOne(String queryId) {
		return sqlSession.selectOne(queryId);
	}

	/**
	 * Select one.
	 *
	 * @param queryId the query id
	 * @return the Object
	 */
	public Optional<Object> selectObject(String queryId) {

		// preInsertPsnInfoLog(queryId, null);
		return sqlSession.selectOne(queryId);
	}

	/**
	 * Select one.
	 *
	 * @param queryId the query id
	 * @param params  the params
	 * @return the map
	 */
	public Object selectOne(String queryId, Object params) {
		return sqlSession.selectOne(queryId, params);

	}

	/**
	 * Select Count.
	 *
	 * @param queryId the query id
	 * @param params  the params
	 * @return the Integer
	 */
	public int selectCount(String queryId, Object params) {
		Integer count = 0;
		count = sqlSession.selectOne(queryId, params);
		return count == null ? 0 : count.intValue();
	}

	/**
	 * Select one.
	 *
	 * @param queryId the query id
	 * @param params  the params
	 * @return the Object
	 */
	public Object selectObject(String queryId, Object params) {
		return sqlSession.selectOne(queryId, params);
	}
	
	
	@SuppressWarnings("unchecked")
	public List<?> selectPage(String queryId, Object params) {
		Map<String,Object> mapParam = (Map<String,Object>)params;
		PageInfo pageInfo = (PageInfo)mapParam.get("pageInfo");
		return sqlSession.selectList(queryId, params,pageInfo);
	}


	/**
	 * Select one.
	 *
	 * @param queryId the query id
	 * @param params  the params
	 * @return the Optional<Object>
	 */
	public Optional<Object> selectOptionalObject(String queryId, Object params) {
		return Optional.ofNullable(sqlSession.selectOne(queryId, params));
	}

	/**
	 * Select one.
	 *
	 * @param queryId the query id
	 * @param params  the params
	 * @return the Object
	 */
	public Optional<Map<String, Object>> selectMap(String queryId, Object params) {
		return Optional.ofNullable(sqlSession.selectOne(queryId, params));
	}

	/**
	 * Select list.
	 *
	 * @param queryId the query id
	 * @return the list
	 */
	public List<?> selectList(String queryId) {
		return sqlSession.selectList(queryId);
	}

	/**
	 * Select list.
	 *
	 * @param queryId the query id
	 * @param params  the params
	 * @return the list
	 */
	public List<?> selectList(String queryId, Object params) {
		return sqlSession.selectList(queryId, params);
	}


	// @SuppressWarnings("unchecked")
	/*
	 * private void preInsertPsnInfoLog(String queryId, Map<String, Object>
	 * paramMap) { String[] psInfoSqlAry = "aa,bb".split(","); for (String psInfoSql
	 * : psInfoSqlAry) { if (queryId.equals(psInfoSql)) { HttpServletRequest request
	 * = ((ServletRequestAttributes) RequestContextHolder
	 * .currentRequestAttributes()).getRequest(); HttpSession session =
	 * request.getSession(); if (session != null) { // paramMap.put("usrId", //
	 * ((Map<String,Object>)session.getAttribute("userInfo")).get("mngrId")); }
	 * String sql =
	 * sqlSession.getConfiguration().getMappedStatement(queryId).getBoundSql(
	 * paramMap).getSql(); List<ParameterMapping> parameterMappings =
	 * sqlSession.getConfiguration().getMappedStatement(queryId)
	 * .getBoundSql(paramMap).getParameterMappings(); if (null != paramMap &&
	 * !paramMap.isEmpty()) { Object o = null; for (ParameterMapping
	 * parameterMapping : parameterMappings) { o =
	 * paramMap.get(parameterMapping.getProperty()); if (o instanceof Integer) { sql
	 * = sql.replaceFirst("\\?", ((Integer) o).toString()); } else { sql =
	 * sql.replaceFirst("\\?", "'" + o + "'"); } } }
	 * 
	 * paramMap.put("inqTxt",sql.trim()); paramMap.put("queryId",queryId);
	 * paramMap.put("cllUrlTxt",
	 * request.getRequestURI().substring(request.getContextPath().length()));
	 * sqlSession.insert("cmmn.insertPsnInfoLog",paramMap);
	 * 
	 * 
	 * } }
	 * 
	 * }
	 */

}