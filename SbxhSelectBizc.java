//********************************************************************* 
//系统名称：PMS2.0
//Copyright(C)2014-2022 NARI Information and Communication Technology Branch. All rights reserved. 
//版本信息：1.0
//#作者:陈磊$权重:100%$手机:18668920163# 
//*********************************************************************
package com.sgcc.pms.dwzy.gg.sbxhselect.bizc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;

import com.sgcc.pms.dwzy.gg.sbxhselect.po.TsbBzzxSbxhb;
import com.sgcc.pms.dwzy.gg.sbxhselect.vo.TsbBzzxSbxhbTransfer;
import com.sgcc.pms.dwzy.gg.sbxhselect.vo.TsbBzzxSbxhbVO;
import com.sgcc.uap.bizc.sysbizc.datadictionary.IDataDictionaryBizC;
import com.sgcc.uap.persistence.IHibernateDao;
import com.sgcc.uap.persistence.criterion.QueryCriteria;
import com.sgcc.uap.rest.support.DicItems;
import com.sgcc.uap.rest.support.IDRequestObject;
import com.sgcc.uap.rest.support.QueryFilter;
import com.sgcc.uap.rest.support.QueryResultObject;
import com.sgcc.uap.rest.support.RequestCondition;
import com.sgcc.uap.rest.utils.CrudUtils;
import com.sgcc.uap.rest.utils.RestUtils;
import com.sgcc.uap.utils.StringUtils;

/**
 * 用户定义逻辑构件
 * 
 * @author CL
 * 
 */
public class SbxhSelectBizc implements ISbxhSelectBizc {

	@Resource
	private IHibernateDao hibernateDao;

	@Resource
	private IDataDictionaryBizC dataDictionaryBizC;

	private String[] queryLikeSblxs = { "1004", "1007", "1008", "1009", "1011" };

	/**
	 * 保存更新
	 * 
	 * @param list
	 */
	public List<TsbBzzxSbxhbVO> saveOrUpdate(List<Map> list) {

		List<TsbBzzxSbxhbVO> voList = new ArrayList<TsbBzzxSbxhbVO>();
		for (int i = 0; i < list.size(); i++) {
			Map map = list.get(i);
			String poName = TsbBzzxSbxhb.class.getName();
			if (map.containsKey("sbxhid")) {
				String id = (String) map.get("sbxhid");
				TsbBzzxSbxhbVO vo = update(map, poName, id);
				voList.add(vo);
			} else {
				TsbBzzxSbxhbVO tsbBzzxSbxhbVO = save(map);
				voList.add(tsbBzzxSbxhbVO);
			}
		}
		return voList;
	}

	// 保存记录
	private TsbBzzxSbxhbVO save(Map map) {

		TsbBzzxSbxhbVO tsbBzzxSbxhbVo = new TsbBzzxSbxhbVO();
		try {
			BeanUtils.populate(tsbBzzxSbxhbVo, map);
		} catch (Exception e) {
			throw new RuntimeException("转换出错", e);
		}
		TsbBzzxSbxhb tsbBzzxSbxhb = TsbBzzxSbxhbTransfer.toPO(tsbBzzxSbxhbVo);
		hibernateDao.saveOrUpdateObject(tsbBzzxSbxhb);
		tsbBzzxSbxhbVo = TsbBzzxSbxhbTransfer.toVO(tsbBzzxSbxhb);
		if (map.containsKey("mxVirtualId")) {
			tsbBzzxSbxhbVo
					.setMxVirtualId(String.valueOf(map.get("mxVirtualId")));
		}
		return tsbBzzxSbxhbVo;
	}

	// 更新记录
	private TsbBzzxSbxhbVO update(Map<String, ?> map, String poName, String id) {

		TsbBzzxSbxhbVO tsbBzzxSbxhbVo = new TsbBzzxSbxhbVO();
		// 更新操作
		try {
			BeanUtils.populate(tsbBzzxSbxhbVo, map);
		} catch (Exception e) {
			throw new RuntimeException("转换出错", e);
		}
		Object[] objArray = CrudUtils.generateHql(TsbBzzxSbxhb.class, map,
				"sbxhid");
		hibernateDao.update((String) objArray[0], (Object[]) objArray[1]);
		return tsbBzzxSbxhbVo;
	}

	/**
	 * 删除
	 * 
	 * @param idObject
	 */
	public void remove(IDRequestObject idObject) {
		String[] ids = idObject.getIds();
		for (int i = 0; i < ids.length; i++) {
			String id = ids[i];
			hibernateDao.update("delete from " + TsbBzzxSbxhb.class.getName()
					+ " where sbxhid = ?", new Object[] { id });
		}
	}

	/**
	 * 根据设备类型获取标准参数
	 * 
	 * @see com.sgcc.pms.dwzy.gg.sbxhselect.bizc.ISbxhSelectBizc#getParam(java.lang.String)
	 */
	public Object getParam(String xh) {
		String sql = "SELECT XLZ AS \"xlz\",XLZZWMC AS \"xlzzwmc\",SBXHBZCSBM AS \"name\",sbxhbzcsmc,sbxhbzcsz AS \"value\", "
				+ "SZSX AS \"sx\",SZXX AS \"xx\",SJLX AS \"sjlx\" from t_dw_bzzx_sbxhbzcsb where sbxhid=?";
		// String sql = SqlFileUtil.get(key);
		String a="select t.obj_id,SBXHBZCSBM AS \"name\",sbxhbzcsz AS \"value\",a.sbxhid,T.NAME, T.CAPTION, T.EDITORTYPE, T.SXHY,a.xlz,a.xlzzwmc"
				+" from T_SB_ZYGL_COLUMNS T, T_DW_BZZX_SBXHBZCSB A, T_SB_PZ_KBQD B"
  				+" where t.sstable = b.obj_id and (a.sblx = b.sblx or b.sblx='wlgt') and a.sbxhbzcsbm=t.name  and a.sbxhid=?";
		String[] xhArr = { xh };
		List list = hibernateDao.queryForListWithSql(a, xhArr);
		return list;
	}

	/**
	 * 根据生产厂家获得国家
	 * 
	 * @see com.sgcc.pms.dwzy.gg.sbxhselect.bizc.ISbxhSelectBizc#getgjBySccjbm(java.lang.String)
	 */
	public String getgjBySccjbm(String sccjbm) {
		String sql = "SELECT GJ FROM T_DW_BZZX_SCCJ WHERE SCCJBM=?";
		List list = hibernateDao.executeSqlQuery(sql, new Object[] { sccjbm });
		if (list.size() > 0) {
			return list.get(0) == null ? "" : list.get(0).toString();
		}
		return "";
	}

	/**
	 * 根据输入条件查询记录
	 * 
	 * @param queryCondition
	 * @return
	 */
	public QueryResultObject query(RequestCondition queryCondition) {
		QueryCriteria qc = new QueryCriteria();
		List<TsbBzzxSbxhb> result = null;
		int count = 0;
		qc.addFrom(TsbBzzxSbxhb.class);
		if (queryCondition != null) {
			qc = wrapQuery(queryCondition, qc);
			count = getRecordCount(qc);
			qc = wrapPage(queryCondition, qc);
			result = hibernateDao.findAllByCriteria(qc);

		} else {
			result = hibernateDao.findAllByCriteria(qc);
			count = getRecordCount(qc);
		}
		return RestUtils.wrappQueryResult(result, count).addDicItems(
				wrapDictList());

	}

	/**
	 * 封装条件查询
	 * 
	 * @param queryCondition
	 * @param qc
	 * @return
	 */
	private QueryCriteria wrapQuery(RequestCondition queryCondition,
			QueryCriteria qc) {
		List<QueryFilter> wheres = queryCondition
				.getQueryFilter(TsbBzzxSbxhbVO.class);
		if (wheres != null && wheres.size() > 0) {
			for (int i = wheres.size() - 1; i >= 0; i--) {
				// 页面传递过来的条件值
				String pidValue = wheres.get(i).getValue().toString();
				// 传递过来的条件名
				String pidName = wheres.get(i).getFieldName();

				if ("sbxh".equals(pidName)) {
					qc.addWhere("", pidName, "like", "%" + pidValue + "%");
					wheres.remove(i);
				} else if ("dydj".equals(pidName)) {
					qc.addWhere("(dydj = '" + pidValue + "' or dydj is null)");
					wheres.remove(i);
				}

				/**
				 * 环网柜 zf07->zf08 箱式变电站 >0324 电缆分支箱 zf09->0204 低压配电箱 zf11->3117
				 * 低压电缆分接箱zf12->3204 低压电缆终端箱zf13->3205
				 * 
				 */
				if ("sssblx".equals(pidName)) {
					if ("zf07".equals(pidValue)) {
						qc.addWhere("", pidName, "=", "0324");
					} else if ("zf08".equals(pidValue)) {
						qc.addWhere("", pidName, "=", "0323");
					} else if ("zf09".equals(pidValue)) {
						qc.addWhere("", pidName, "=", "0204");
					} else if ("zf11".equals(pidValue)) {
						qc.addWhere("", pidName, "=", "3117");
					} else if ("zf12".equals(pidValue)) {
						qc.addWhere("", pidName, "=", "3204");
					} else if ("zf13".equals(pidValue)) {
						qc.addWhere("", pidName, "=", "3205");
					} else if ("0103002".equals(pidValue)) {
						qc.addWhere(" " + pidName + " like '0103002%'");
					} else if (contains(pidValue)) {
						qc.addWhere(" " + pidName + " like '" + pidValue + "%'");
					} else if (pidValue.indexOf("0190@") >= 0) {// 线路附属设施要特殊处理
						String[] fsSblx = pidValue.split("@");
						String realSblx = "0190";
						if (fsSblx.length > 1) {
							realSblx = fsSblx[1];
						}
						qc.addWhere(" sssblx in('0190','" + realSblx + "') ");
					} else if ("xl,0101,0201".equals(pidValue)) {
						String realVal = "'" + pidValue.replaceAll(",", "','")
								+ "'";
						qc.addWhere(" sssblx in (" + realVal + ") ");
					} else if(pidValue.matches("^040[1-3]0[0-1]\\d$")){//换流站控包系统下级设备特殊处理
						String filter = wrapHlzkbxtFilter(pidValue);
						if(StringUtils.isNotEmpty(filter)){
							qc.addWhere(filter);
						}
					} else {
						qc.addWhere("", pidName, "=", pidValue);
					}
					wheres.remove(i);
				}
			}
			CrudUtils.addQCWhere(qc, wheres, TsbBzzxSbxhb.class.getName());
		}

		String orders = queryCondition.getSorter();
		if (orders != null) {
			qc.addOrder(orders.replaceAll("&", ","));
		}
		return qc;
	}

	/**
	 * 换流站控包系统下级设备特殊处理
	 * @param sblx 设备类型
	 * @return
	 */
	private String wrapHlzkbxtFilter(String sblx){
		String[] lastArray = {"3","4","5","0","1","2"};
		if(StringUtils.isNullOrEmpty(sblx)){
			return "";
		}
		String filter ="";
		if(sblx.matches("040[1-3]00[1-9]")){
			String start = sblx.substring(0,3);
			String end  =  sblx.substring(4);
			filter = " SSSBLX IN('"+start+"1"+end+"','"+start+"2"+end+"','"+start+"3"+end+"')";
		}else if(sblx.matches("040101[0-2]")){
			filter =" SSSBLX ='"+sblx+"'";
		}else if(sblx.matches("040101[3-5]")){
			String lastChar = sblx.substring(sblx.length()-1);
			if(StringUtils.isNumeric(lastChar)){
				String last = lastArray[Integer.valueOf(lastChar)];
				filter = " SSSBLX IN('"+sblx+"','040201"+last+"','040301"+last+"')";
			}
		}else if(sblx.matches("040[2-3]01[0-2]")){
			String lastChar = sblx.substring(sblx.length()-1);
			if(StringUtils.isNumeric(lastChar)){
				String last = lastArray[Integer.valueOf(lastChar)];
				String start1 = sblx.substring(0,3);
				String last1 = sblx.substring(4);
				filter = " SSSBLX IN('"+start1+"2"+last1+"','"+start1+"3"+last1+"','040101"+last+"')" ;
			}
		}
		return filter;
	}
	
	/**
	 * 封装分页查询
	 * 
	 * @param queryCondition
	 * @param qc
	 * @return
	 */
	private QueryCriteria wrapPage(RequestCondition queryCondition,
			QueryCriteria qc) {
		int pageIndex = 1, pageSize = 1;
		if (queryCondition.getPageIndex() != null
				&& queryCondition.getPageSize() != null) {
			pageIndex = Integer.valueOf(queryCondition.getPageIndex());
			pageSize = Integer.valueOf(queryCondition.getPageSize());
			qc.addPage(pageIndex, pageSize);
		}
		return qc;
	}

	/**
	 * 查询单条记录
	 * 
	 * @param id
	 * @return QueryResultObject
	 */
	public QueryResultObject queryById(String id) {
		TsbBzzxSbxhb tsbBzzxSbxhb = (TsbBzzxSbxhb) hibernateDao.getObject(
				TsbBzzxSbxhb.class, id);
		TsbBzzxSbxhbVO vo = null;
		if (tsbBzzxSbxhb != null) {
			vo = TsbBzzxSbxhbTransfer.toVO(tsbBzzxSbxhb);
		}
		return RestUtils.wrappQueryResult(vo).addDicItems(wrapDictList());
	}

	/**
	 * 初始化字典值
	 * 
	 * @return QueryResultObject
	 */
	public QueryResultObject initDict() {
		QueryResultObject query = new QueryResultObject();
		return query.addDicItems(wrapDictList());
	}

	// 将字典对象封装为list
	private List<DicItems> wrapDictList() {
		List<DicItems> dicts = new ArrayList<DicItems>();

		dicts.add(translateFromDB("objId",
				"com.sgcc.pms.dwzy.gg.sbxhselect.po.TsbBzzxBzxxpz", "objId",
				"kbzwmc", "", ""));
		dicts.add(translateFromDB("dydj",
				"com.sgcc.pms.dwzy.gg.sbxhselect.po.TsbBzzxGgdmb", "dm",
				"dmmc", "bzflbm = '010401'", "dm desc"));
		dicts.add(translateFromDB("sssblx",
				"com.sgcc.pms.dwzy.gg.sbxhselect.po.TsbBzzxSbfl", "sblxbm",
				"sblx", "", ""));
        dicts.add(translateFromDB("sfcyk",
        		"com.sgcc.pms.dwzy.bzzx.sbxhbzcswhsbxh.po.TsbBzzxGgdmb", "dm",
        		"dmmc", "bzflbm = '010599'", "px asc"));
		return dicts;
	}

	// 从数据库中查询字典
	private DicItems translateFromDB(String fieldName, String poName,
			String keyField, String valueField, String where, String order) {
		List<Map<String, String>> list = dataDictionaryBizC.translateFromDB(
				poName, "value", "text", keyField, valueField, where, order);
		DicItems dict = new DicItems();
		dict.setName(fieldName);
		dict.setValues(list);
		return dict;
	}

	/**
	 * 注入数据字典构件
	 * 
	 * @param dataDictionaryBizC
	 */
	public void setDataDictionaryBizC(IDataDictionaryBizC dataDictionaryBizC) {
		this.dataDictionaryBizC = dataDictionaryBizC;
	}

	// 获取总记录数
	private int getRecordCount(QueryCriteria qc) {
		int count = 0;
		count = hibernateDao.findAllByCriteriaPageCount(qc, 1);
		return count;
	}

	private boolean contains(Object obj) {
		return indexOf(obj) >= 0;
	}

	private int indexOf(Object obj) {
		if (obj == null) {
			for (int i = 0; i < queryLikeSblxs.length; i++)
				if (queryLikeSblxs[i] == null)
					return i;
		} else {
			for (int j = 0; j < queryLikeSblxs.length; j++)
				if (obj.equals(queryLikeSblxs[j]))
					return j;
		}
		return -1;
	}

	/**
	 * 
	 * <b>功能</b>：<br/>
	 * 
	 * @param hibernateDao
	 */
	public void setHibernateDao(IHibernateDao hibernateDao) {
		this.hibernateDao = hibernateDao;
	}

}
