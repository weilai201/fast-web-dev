package com.fastweb.core.page;

import java.util.List;

public class PageContext {
	
	/**
	 * 分页信息
	 */
	private Pagination page;
	
	/**
	 * 数据
	 */
	private List list;

	public Pagination getPage() {
		return page;
	}

	public void setPage(Pagination page) {
		this.page = page;
	}

	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}
}
