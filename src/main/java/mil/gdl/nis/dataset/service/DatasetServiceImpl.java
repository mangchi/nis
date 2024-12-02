package mil.gdl.nis.dataset.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mil.gdl.nis.cmmn.SessionData;
import mil.gdl.nis.cmmn.service.FileStorageService;
import mil.gdl.nis.dao.DAO;
import mil.gdl.nis.dataset.vo.DatasetVo;
import mil.gdl.nis.img.service.ImgService;
import mil.gdl.nis.mov.service.MovService;
import mil.gdl.nis.user.vo.Authority;

@Slf4j
@RequiredArgsConstructor
@Service
public class DatasetServiceImpl implements DatasetService{
	

    private final DAO dao;
    private final ImgService imgService;
    private final MovService movService;
    private final SessionData sessionData;
    private final FileStorageService fileStorageService;

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getDatasetList(Map<String, Object> map) throws Exception {
		return (List<Map<String, Object>>)dao.selectPage("Dataset.selectDatasetList", map);
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getDataList(Map<String, Object> map) throws Exception {
		map.put("userId", sessionData.getUserVo().getUserId());
		if(String.valueOf(sessionData.getUserVo().getAuthority()).equals(Authority.ROLE_ADMIN.name())) {
			map.put("userId", "ALL");
		}
		return (List<Map<String, Object>>)dao.selectPage("Dataset.selectDataList", map);
	}

	@Override
	public Map<String, Object> getDatasetInfo(Map<String, Object> map) throws Exception {
		
		return dao.selectMap("Dataset.select", map).orElseGet(() -> new HashMap<String, Object>());
	}

	@Override
	public DatasetVo getDataset(Map<String, Object> map) throws Exception {
		
		return (DatasetVo)dao.selectOptionalObject("Dataset.selectVo", map).orElseGet(() -> new DatasetVo());
	}

	@SuppressWarnings("unchecked")
	@Override
	public int delete(Map<String, Object> map) throws Exception {
		int rtn = 0;
		List<Map<String,Object>> deleteList = (List<Map<String,Object>>)map.get("list");
		for(Map<String,Object> item:deleteList) {
			if(item.get("stillOrMovie").equals("001")) {
				rtn += dao.delete("Img.deleteImgByParentId",item);
			}else {
				rtn += dao.delete("Mov.deleteMovByParentId",item);
			}
		}
		rtn += dao.delete("Dataset.deleteList",map);
		rtn += fileStorageService.deleteAllFile(map);
		return rtn;
	}

	@Override
	public int save(DatasetVo vo) throws Exception {
		int rtn = 0;
		try {
			vo.setId(vo.getFileId());
			vo.setCreateUserId(sessionData.getUserVo().getUserId());
			vo.setUpdateUserId(sessionData.getUserVo().getUserId());
			rtn += dao.update("Dataset.mergeDataset",vo);
			if(vo.getIsChangeFile().equals("Y")){
				Map<String,Object> paramMap = new HashMap<String,Object>();
				paramMap.put("list", fileStorageService.selectList(vo.getFileId()));
				if(vo.getStillOrMovie().equals("001")) {
					rtn +=  imgService.save(paramMap);
				}
				else {
					rtn +=  movService.save(paramMap);
				}
			}

		}catch(Exception e) {
			
		}
		return rtn;
	}


	


}
