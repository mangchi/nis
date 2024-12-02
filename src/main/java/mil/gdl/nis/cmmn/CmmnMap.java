package mil.gdl.nis.cmmn;

import org.apache.commons.collections4.map.ListOrderedMap;

import mil.gdl.nis.cmmn.util.StringUtil;

public class CmmnMap extends ListOrderedMap<Object, Object> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Object put(Object key, Object value) {
		return super.put(StringUtil.convertCamelCase((String) key), value);
	}

}
