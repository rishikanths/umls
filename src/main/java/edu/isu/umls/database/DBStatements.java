package edu.isu.umls.database;

public interface DBStatements {
	
	public String HIBERNATE_SESSION_FACTORY = "HIBERNATE_SESSION_FACTORY";

	public String SEARCH_QUERY = "SELECT CUI, STR from MRCONSO WHERE TS = 'P' AND STT='PF' AND ISPREF='Y'"
			+ "AND LAT='ENG' AND STR LIKE ?";
	
	public String SEARCH_BY_CUI = "SELECT CUI, STR from umls.MRCONSO WHERE TS = 'P' AND STT='PF' AND ISPREF='Y'"
			+ " AND LAT='ENG' AND CUI = ?";
	
	public String SEARCH_BY_AUI = "SELECT CUI from umls.MRCONSO WHERE AUI = ?";
	
	public String SEARCH_SEMANTIC_TYPE_BY_CUI = "SELECT TUI, STY from umls.MRSTY WHERE CUI = ?";

	/*public String SEARCH_CONCEPT_HIERARCHY = "SELECT DISTINCT r.CUI1,c.CUI,c.STR,r.REL, r.RELA, st.TUI,st.STY"
			+ " FROM umls.MRCONSO as c, umls.MRREL as r, umls.MRSTY as st WHERE"
			+ " c.CUI = ? AND c.CUI = r.CUI2 AND c.CUI = st.CUI AND r.CUI1 <> c.CUI"
			+ " AND r.rel IN ('PAR','CHD') AND c.TS = 'P' AND c.STT='PF' AND c.ISPREF='Y'" + " GROUP BY r.CUI1";*/
	
	/*public String SEARCH_CONCEPT_RELATIONS = "SELECT DISTINCT r.CUI1,c.CUI,c.STR,r.REL, r.RELA, st.TUI,st.STY"
			+ " FROM umls.MRCONSO as c, umls.MRREL as r, umls.MRSTY as st WHERE"
			+ " c.CUI = ? AND c.CUI = r.CUI2 AND c.CUI = st.CUI AND r.CUI1 <> c.CUI"
			+ " AND r.rel IN ('QB','RO','RU',NULL) AND c.TS = 'P' AND c.STT='PF' AND c.ISPREF='Y'" + " GROUP BY r.CUI1";*/

	
	public String SEARCH_CONCEPT = "SELECT r.CUI1, C1.STR, S1.STY,S1.TUI,"
			+" r.REL, r.RELA, r.CUI2, C2.STR, S2.STY, S2.TUI"
			+" FROM MRREL AS r"
			+" LEFT JOIN MRCONSO AS C1 ON r.CUI1 = C1.CUI"
			+" LEFT JOIN MRCONSO AS C2 ON r.CUI2 = C2.CUI"
			+" LEFT JOIN MRSTY AS S1 ON r.CUI1 = S1.CUI"
			+" LEFT JOIN MRSTY AS S2 ON r.CUI2 = S2.CUI"			   
			+" WHERE C1.TS='P' AND C1.STT='PF' AND C1.ISPREF='Y' AND C1.LAT='ENG'"
			+" AND C2.TS='P' AND C2.STT='PF' AND C2.ISPREF='Y' AND C2.LAT='ENG'"
			+" AND r.CUI2 = ?";
	
	public String SEARCH_CONCEPT_HIERARCHY = SEARCH_CONCEPT+ " AND r.CUI1 <> r.CUI2 AND r.REL IN('PAR')";
	
	public String SEARCH_CONCEPT_RELATIONS = SEARCH_CONCEPT+ " AND r.REL IN('QB','RO','RU',NULL)";

	
	
	public String SEARCH_SYNONYMS = "SELECT  c.str as STR, v.son as SOURCE FROM umls.MRCONSO as c, umls.MRSAB as v WHERE c.CUI = ? "
			+ "AND c.sab = v.rsab";
	
	public String SEARCH_CONCEPT_DEFINITION = "SELECT DISTINCT s.SON ,c.STR, d.DEF,d.SAB FROM umls.MRDEF as d, umls.MRCONSO as c, umls.MRSAB as s  "
			+ "WHERE d.CUI = c.CUI AND c.CUI = ? AND c.TS = 'P' AND c.STT='PF' AND c.ISPREF='Y' AND c.LAT='ENG' AND d.SAB = s.RSAB GROUP BY d.SAB";
}
