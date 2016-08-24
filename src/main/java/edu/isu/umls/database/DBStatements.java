package edu.isu.umls.database;

public interface DBStatements {

	public String SEARCH_QUERY = "SELECT CUI, STR from MRCONSO WHERE TS = 'P' AND STT='PF' AND ISPREF='Y'"
			+ " AND STR LIKE ? LIMIT ?";

	public String SEARCH_BY_CUI = "SELECT CUI, STR from MRCONSO WHERE TS = 'P' AND STT='PF' AND ISPREF='Y'"
			+ " AND CUI = ?";

	public String SEARCH_BY_AUI = "SELECT CUI from MRCONSO WHERE AUI = ?";

	public String SEARCH_SEMANTIC_TYPE_BY_CUI = "SELECT TUI, STY from MRSTY WHERE CUI = ?";

	public String SEARCH_CONCEPT_HIERARCHY = "SELECT DISTINCT r.CUI1,c.CUI,c.STR,r.REL, r.RELA, st.TUI,st.STY"
			+ " FROM MRCONSO as c, MRREL as r, MRSTY as st WHERE"
			+ " c.CUI = ? AND c.CUI = r.CUI2 AND c.CUI = st.CUI AND r.CUI1 <> c.CUI"
			+ " AND r.rel IN ('PAR','CHD') AND c.TS = 'P' AND c.STT='PF' AND c.ISPREF='Y'" + " GROUP BY r.CUI1";

	public String SEARCH_CONCEPT_RELATIONS = "SELECT DISTINCT r.CUI1,c.CUI,c.STR,r.REL, r.RELA, st.TUI,st.STY"
			+ " FROM MRCONSO as c, MRREL as r, MRSTY as st WHERE"
			+ " c.CUI = ? AND c.CUI = r.CUI2 AND c.CUI = st.CUI AND r.CUI1 <> c.CUI"
			+ " AND r.rel IN ('QB','RO','RU',NULL) AND c.TS = 'P' AND c.STT='PF' AND c.ISPREF='Y'" + " GROUP BY r.CUI1";

	public String SEARCH_SYNONYMS = "SELECT  c.str as STR, v.son as SOURCE FROM MRCONSO as c, MRSAB as v WHERE c.CUI = ? "
			+ "AND c.sab = v.rsab";
	
	public String SEARCH_CONCEPT_DEFINITION = "SELECT DISTINCT s.SON ,c.STR, d.DEF,d.SAB FROM MRDEF as d, MRCONSO as c, MRSAB as s  "
			+ "WHERE d.CUI = c.CUI AND c.CUI = ? AND c.TS = 'P' AND c.STT='PF' AND c.ISPREF='Y' AND d.SAB = s.RSAB";
}
