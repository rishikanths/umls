package edu.isu.umls.database;

public interface DBStatements {

	public String SEARCH_QUERY = "SELECT CUI, STR from MRCONSO WHERE TS = 'P' AND STT='PF' AND ISPREF='Y'"
			+ " AND STR LIKE ? LIMIT ?";

	public String SEARCH_BY_CUI = "SELECT CUI, STR from MRCONSO WHERE TS = 'P' AND STT='PF' AND ISPREF='Y'"
			+ " AND CUI = ?";

	public String SEARCH_BY_AUI = "SELECT CUI from MRCONSO WHERE AUI = ?";
	
	public String SEARCH_SEMANTIC_TYPE_BY_CUI = "SELECT TUI, STY from MRSTY WHERE CUI = ?";
	
	public String SEARCH_CONCEPT_HIERARCHY = "SELECT DISTINCT r.CUI1,c.CUI,c.STR,r.REL, r.RELA, st.TUI,st.STY"
			+ " FROM mrconso as c, mrrel as r, mrsty as st WHERE"
			+ " c.CUI = ? AND c.CUI = r.CUI2 AND c.CUI = st.CUI AND r.CUI1 <> c.CUI"
			+ " AND r.rel IN ('PAR','CHD') AND c.TS = 'P' AND c.STT='PF' AND c.ISPREF='Y'"
			+ " GROUP BY r.CUI1";
	
	public String SEARCH_CONCEPT_RELATIONS = "SELECT DISTINCT r.CUI1,c.CUI,c.STR,r.REL, r.RELA, st.TUI,st.STY"
			+ " FROM mrconso as c, mrrel as r, mrsty as st WHERE"
			+ " c.CUI = ? AND c.CUI = r.CUI2 AND c.CUI = st.CUI AND r.CUI1 <> c.CUI"
			+ " AND r.rel IN ('QB','RO','RU',NULL) AND c.TS = 'P' AND c.STT='PF' AND c.ISPREF='Y'"
			+ " GROUP BY r.CUI1";
}
