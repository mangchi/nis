package mil.gdl.nis.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import mil.gdl.nis.cmmn.vo.PageInfo;

@Slf4j
@Component
/*
 * @Intercepts({ @Signature(type = Executor.class, method = "query", args = {
 * MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class })
 * })
 */
@Intercepts ( { @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}) 
,@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}) } )
public class QueryInterceptor implements Interceptor {

	private static String COUNT_ID_SUFFIX = "TotalCnt";

	/**
	 * @Method : createCountResultMaps
	 * @CreateDate : 2021. 4. 19.
	 * @param ms
	 * @return
	 * @Description : COUNT QUERY 결과를 받기위한 ResultMaps 생성
	 */
	private List<ResultMap> createCountResultMaps(MappedStatement ms) {
		List<ResultMap> countResultMaps = new ArrayList<>();

		ResultMap countResultMap = new ResultMap.Builder(ms.getConfiguration(), ms.getId() + COUNT_ID_SUFFIX,
				Long.class, new ArrayList<>()).build();
		countResultMaps.add(countResultMap);

		return countResultMaps;
	}

	/**
	 * @Method : createCountMappedStatement
	 * @CreateDate : 2021. 4. 19.
	 * @param ms
	 * @return
	 * @Description : COUNT QUERY 결과를 받기위한 MappedStatement 생성 속도문제로 개선필요 시 간단히 Map으로
	 *              캐싱처리해도 될듯
	 */
	private MappedStatement createCountMappedStatement(MappedStatement ms) {
		List<ResultMap> countResultMaps = createCountResultMaps(ms);
		return new MappedStatement.Builder(ms.getConfiguration(), ms.getId() + COUNT_ID_SUFFIX, ms.getSqlSource(),
				ms.getSqlCommandType()).resource(ms.getResource()).parameterMap(ms.getParameterMap())
						.resultMaps(countResultMaps).fetchSize(ms.getFetchSize()).timeout(ms.getTimeout())
						.statementType(ms.getStatementType()).resultSetType(ms.getResultSetType()).cache(ms.getCache())
						.flushCacheRequired(ms.isFlushCacheRequired()).useCache(true)
						.resultOrdered(ms.isResultOrdered()).keyGenerator(ms.getKeyGenerator())
						.keyColumn(ms.getKeyColumns() != null ? String.join(",", ms.getKeyColumns()) : null)
						.keyProperty(ms.getKeyProperties() != null ? String.join(",", ms.getKeyProperties()) : null)
						.databaseId(ms.getDatabaseId()).lang(ms.getLang())
						.resultSets(ms.getResultSets() != null ? String.join(",", ms.getResultSets()) : null).build();
	}

	/**
	 * @Method : createPagableResponse
	 * @CreateDate : 2021. 4. 19.
	 * @param list
	 * @param pageInfo
	 * @return
	 * @Description : 최종적으로 return할 PagableResponse 생성
	 */
	/*
	 * private Map<String,Object> createPagableResponse(List<Object> list, PageInfo
	 * pageInfo) {
	 * 
	 * PagableResponse<Object> pagableResponse = new PagableResponse<>();
	 * pagableResponse.setList(list);
	 * pagableResponse.getPageInfo().setPage(pageInfo.getPage());
	 * pagableResponse.getPageInfo().setSize(pageInfo.getSize());
	 * pagableResponse.getPageInfo().setTotalCount(pageInfo.getTotalCount());
	 * 
	 * return pagableResponse; }
	 */

	@SuppressWarnings("unchecked")
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		try {
			if(null != invocation.getArgs()[2] && invocation.getArgs()[2] instanceof PageInfo) {
				PageInfo pageInfo = (PageInfo) invocation.getArgs()[2];
	            
				MappedStatement originalMappedStatement = (MappedStatement) invocation.getArgs()[0];
				MappedStatement countMappedStatement = createCountMappedStatement(originalMappedStatement);
				// COUNT 구하기
				invocation.getArgs()[0] = countMappedStatement;
				List<Long> totalCount = (List<Long>) invocation.proceed();
				pageInfo.setTotalCount(totalCount.get(0));
				if(pageInfo.getTotalCount()%pageInfo.getRowPerPage() == 0) {
					pageInfo.setTotalPage(Integer.parseInt(String.valueOf(pageInfo.getTotalCount()/pageInfo.getRowPerPage())));
				}else {
					pageInfo.setTotalPage(Integer.parseInt(String.valueOf(pageInfo.getTotalCount()/pageInfo.getRowPerPage()))+1);
				}
				
				pageInfo.setIsFirstPage(pageInfo.getPageNum()==1?true:false);
				pageInfo.setIsLastPage(pageInfo.getPageNum()==pageInfo.getTotalPage()?true:false);
	
				// LIST 구하기
				List<Object> list = new ArrayList<>();
				if(pageInfo.getTotalCount() == 0) {
					return list;
				}
				invocation.getArgs()[0] = originalMappedStatement;
				//log.debug("originalMappedStatement:{}",originalMappedStatement.getBoundSql(invocation.getArgs()[1]));
				list = (List<Object>) invocation.proceed();
				// return createPagableResponse(list, pageInfo);
	            /*
				Map<String, Object> pageList = new HashMap<String, Object>();
				pageList.put("list", list);
				pageList.put("pageNum", pageInfo.getPageNum());
				pageList.put("rowPerPage", pageInfo.getRowPerPage());
				pageList.put("totCnt", pageInfo.getTotalCount());
			 
				return pageList;
				*/
	            return list;
			}
		} catch (ClassCastException e) {
			log.error("error:", e );
		}
	
		return invocation.proceed();
	}

}
