/*
 * Copyright 2003, 2004  The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ws.jaxme.sqls.junit;

import org.apache.ws.jaxme.sqls.BooleanConstraint;
import org.apache.ws.jaxme.sqls.Column;
import org.apache.ws.jaxme.sqls.ColumnReference;
import org.apache.ws.jaxme.sqls.CombinedConstraint;
import org.apache.ws.jaxme.sqls.DeleteStatement;
import org.apache.ws.jaxme.sqls.Function;
import org.apache.ws.jaxme.sqls.JoinReference;
import org.apache.ws.jaxme.sqls.Schema;
import org.apache.ws.jaxme.sqls.SelectStatement;
import org.apache.ws.jaxme.sqls.SelectTableReference;
import org.apache.ws.jaxme.sqls.Table;
import org.apache.ws.jaxme.sqls.TableReference;
import org.apache.ws.jaxme.sqls.impl.VirtualColumn;
import org.apache.ws.jaxme.sqls.oracle.OraColumnReference;
import org.apache.ws.jaxme.sqls.oracle.OraSQLFactory;
import org.apache.ws.jaxme.sqls.oracle.OraSQLFactoryImpl;
import org.apache.ws.jaxme.sqls.oracle.OraSQLGenerator;
import org.apache.ws.jaxme.sqls.oracle.OraSelectStatement;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class JoinTest extends TestCase {
    private OraSQLFactory sqlFactory;
    private OraSQLGenerator sqlGenerator;
    private Schema schema;
    private Table dbAkte, dbBeteiligte, dbAktenzeichen;
    private Table kettenElement, vertreterKette;

    /** <p>Creates a new instance with the given name.</p>
     */
    public JoinTest(String pName) {
        super(pName);
    }

    public void setUp() {
        sqlFactory = new OraSQLFactoryImpl();
        sqlGenerator = (OraSQLGenerator) sqlFactory.newSQLGenerator();
        sqlGenerator.setOracle8Compatibility(true);
        schema = sqlFactory.getDefaultSchema();
        dbAkte = schema.newTable("DBAkte");
        dbAkte.newColumn("aId", Column.Type.BIGINT);
        dbBeteiligte = schema.newTable("DBBeteiligte");
        dbBeteiligte.newColumn("aAktenId", Column.Type.BIGINT);
        dbBeteiligte.newColumn("aFilter", Column.Type.BIGINT);
        dbBeteiligte.newColumn("aName", Column.Type.VARCHAR);
        dbBeteiligte.newColumn("aVorname", Column.Type.VARCHAR);
        dbBeteiligte.newColumn("aId", Column.Type.BIGINT);
        dbAktenzeichen = schema.newTable("DBAktenzeichen");
        dbAktenzeichen.newColumn("aAktenId", Column.Type.BIGINT);
        dbAktenzeichen.newColumn("aFilter", Column.Type.VARCHAR);
        dbAktenzeichen.newColumn("aId", Column.Type.BIGINT);

        kettenElement = schema.newTable("KettenElement");
        kettenElement.newColumn("aId", Column.Type.BIGINT);
        kettenElement.newColumn("aVertreterId", Column.Type.BIGINT);
        kettenElement.newColumn("organisationsId", Column.Type.BIGINT);

        vertreterKette = schema.newTable("VertreterKette");
        vertreterKette.newColumn("wurzelElement", Column.Type.BIGINT);
    }

    /** <p>Creates the WHERE clause
     * <pre>
     *   a.aID = DBBeteiligte.aAktenId
     * </pre></p>
     */
    private void addAktenId(CombinedConstraint pWhere,
							TableReference pAkteReference, TableReference pBeteiligteReference) {
        BooleanConstraint bc = pWhere.createEQ();
        bc.addPart(pBeteiligteReference.newColumnReference(dbBeteiligte.getColumn("aAktenId")));
        bc.addPart(pAkteReference.newColumnReference(dbAkte.getColumn("aId")));
    }

	private void addEQ(TableReference pTableReference, CombinedConstraint pWhere,
					   Column pColumn, String pValue) {
        BooleanConstraint bc = pWhere.createEQ();
        bc.addPart(pTableReference.newColumnReference(pColumn));
        bc.addPart(pValue);
	}

    /** <p>Creates the count statement
     * <pre>
     * 	 (SELECT COUNT(*) AS pColumnName FROM DBBeteiligte WHERE aAktenId=a.aId AND aFilter=pFilter) AS pColumnName
     * </pre></p>
     */
    private ColumnReference getCountStatement(String pColumnName, String pFilter,
											  TableReference pAkteReference, String pTableAlias) {
        // SELECT COUNT(*) FROM DBBeteiligte klc WHERE klc.aFilter='Klaeger' AND klc.aAktenId=a.aID
        SelectStatement st = sqlFactory.newSelectStatement();
        st.setTable(dbBeteiligte);
        SelectTableReference tRef = st.getSelectTableReference();
        tRef.setAlias(pTableAlias);
        addEQ(tRef, st.getWhere(), dbBeteiligte.getColumn("aFilter"), pFilter);
        addAktenId(st.getWhere(), pAkteReference, tRef);
        VirtualColumn vc = new VirtualColumn(pColumnName, Column.Type.INTEGER);
        vc.setValue("COUNT(*)");
        st.addResultColumn(vc);

        VirtualColumn result = new VirtualColumn(pColumnName, Column.Type.INTEGER);
        result.setValue(st);
        return result;
    }


    /** <p>Creates a statement fetching the first row matching the
     * search criteria:
     * <pre>
     *   LEFT OUTER JOIN
     *     (SELECT NUM, aName, aVorname, aAktenId FROM
     *         (SELECT COUNT(*) OVER (PARTITION BY aAktenId) NUM,
     *                 MIN(UPPER(aName)) OVER (PARTITION BY aAktenId) MINANAME,
     *                 MIN(UPPER(NVL(aVorname, ' '))) OVER (PARTITION BY aAktenId, UPPER(aName)) MINAVORNAME,
     *                 aName, aVorname, aAktenId
     *          FROM DBBeteiligte WHERE aFilter=pFilter
     *         )
     *      WHERE UPPER(aName)=MAXANAME AND UPPER(NVL(aVorname, ' '))=MAXAVORNAME
     *     ) pTableAlias ON a.aId = pTableAlias.aAktenId
     * </pre>
     */
	private JoinReference getFirstRowStatement2(String pFilter, TableReference pAkteReference,
	        									SelectTableReference pJoinReference,
	        									String pTableAlias) {
	    Column aName = dbBeteiligte.getColumn("aName");
        Column aVorname = dbBeteiligte.getColumn("aVorname");
        Column aAktenId = dbBeteiligte.getColumn("aAktenId");
        Column aFilter = dbBeteiligte.getColumn("aFilter");

        SelectStatement st = sqlFactory.newSelectStatement();
        st.setTable(dbBeteiligte);
        SelectTableReference ref = st.getSelectTableReference();
        VirtualColumn num = new VirtualColumn("NUM", Column.Type.INTEGER);
        num.setValue("COUNT(*) OVER (PARTITION BY " + aAktenId.getName() + ")");
        st.addResultColumn(num);
        VirtualColumn minAName = new VirtualColumn("MINANAME", Column.Type.VARCHAR);
        minAName.setValue("MIN(UPPER(" + aName.getName() + ")) OVER (PARTITION BY " +
                		  aAktenId.getName() + ")");
        st.addResultColumn(minAName);
        VirtualColumn minAVorname = new VirtualColumn("MINAVORNAME", Column.Type.VARCHAR);
        minAVorname.setValue("MIN(UPPER(NVL(" + aVorname.getName() +
                			 ", ' '))) OVER (PARTITION BY " +
                	 		 aAktenId.getName() + ", UPPER(" + aName.getName() +
                	 		 "))");
        st.addResultColumn(minAVorname);
        st.addResultColumn(ref.newColumnReference(aName));
        st.addResultColumn(ref.newColumnReference(aVorname));
        st.addResultColumn(ref.newColumnReference(aAktenId));
        BooleanConstraint bc = st.getWhere().createEQ();
        bc.addPart(st.getTableReference().newColumnReference(aFilter));
        bc.addPart(pFilter);
        Table t = st.createView((Table.Name) null);

        SelectStatement st2 = sqlFactory.newSelectStatement();
        st2.setTable(t);
        SelectTableReference ref2 = st2.getSelectTableReference();
        st2.addResultColumn(ref2.newColumnReference(t.getColumn("NUM")));
        Column aName2 = t.getColumn(aName.getName());
        st2.addResultColumn(ref2.newColumnReference(aName2));
        Column aVorname2 = t.getColumn(aVorname.getName());
        st2.addResultColumn(ref2.newColumnReference(aVorname2));
        st2.addResultColumn(ref2.newColumnReference(t.getColumn(aAktenId.getName())));

        bc = st2.getWhere().createEQ();
        bc.addPart(ref2.newColumnReference(t.getColumn("MINAVORNAME")));
        Function f = st2.createFunction("MIN");
        Function f2 = st.createFunction("UPPER");
        Function f3 = st.createFunction("NVL");
        f3.addPart(ref2.newColumnReference(aVorname2));
        f3.addPart(" ");
        f2.addPart(f3);
        bc.addPart(f);

        bc = st2.getWhere().createEQ();
        bc.addPart(ref2.newColumnReference(t.getColumn("MINANAME")));
        f = st2.createFunction("MIN");
        f2 = st.createFunction("UPPER");
        f2.addPart(ref2.newColumnReference(aName2));
        f.addPart(f2);
        bc.addPart(f);
        Table t2 = st2.createView(pTableAlias);

        JoinReference result = pJoinReference.leftOuterJoin(t2);
        bc = result.getOn().createEQ();
        bc.addPart(pAkteReference.newColumnReference(dbAkte.getColumn("aId")));
        bc.addPart(result.newColumnReference(t2.getColumn(aAktenId.getName())));
        return result;
	}

    /** <p>Creates a statement fetching the first row matching the search criteria:
     * <pre>
     *   LEFT OUTER JOIN DBBeteiligte pTableAlias
     *   ON a.aId=pTableAlias.aAktenId AND pTableAlias.aFilter=pFilter AND
     *     UPPER(pTableAlias.aName)=
     *      (SELECT MIN(UPPER(aName)) FROM DBBeteiligte min WHERE
     *         pTableAlias.aAktenId=min.aAktenId AND min.aAktenId=pFilter)
     * </pre></p>
     */
    private JoinReference getFirstRowStatement(String pFilter, TableReference pAkteReference,
											   SelectTableReference pJoinReference,
											   String pTableAlias) {
        JoinReference result = pJoinReference.leftOuterJoin(dbBeteiligte);
        result.setAlias(pTableAlias);
        addAktenId(result.getOn(), pAkteReference, result);
        addEQ(result, result.getOn(), dbBeteiligte.getColumn("aFilter"), pFilter);
        BooleanConstraint bc = result.getOn().createEQ();
        
        Function f = pAkteReference.getStatement().createFunction("UPPER");
        f.addPart(result.newColumnReference(dbBeteiligte.getColumn("aName")));
        bc.addPart(f);
        
        SelectStatement minStatement = sqlFactory.newSelectStatement();
        minStatement.setTable(dbBeteiligte);
        SelectTableReference minTableRef = minStatement.getSelectTableReference();
        minTableRef.setAlias(pTableAlias + "min");
        BooleanConstraint bc2 = minStatement.getWhere().createEQ();
        bc2.addPart(result.newColumnReference(dbBeteiligte.getColumn("aAktenId")));
        bc2.addPart(minTableRef.newColumnReference(dbBeteiligte.getColumn("aAktenId")));
        
        bc2 = minStatement.getWhere().createEQ();
        bc2.addPart(minTableRef.newColumnReference(dbBeteiligte.getColumn("aFilter")));
        bc2.addPart(pFilter);

        f = pAkteReference.getStatement().createFunction("MIN");
        Function f2 = pAkteReference.getStatement().createFunction("UPPER");
        f.addPart(f2);
        f2.addPart(minTableRef.newColumnReference(dbBeteiligte.getColumn("aName")));
        VirtualColumn vc = new VirtualColumn("MIN", Column.Type.VARCHAR);
        vc.setValue(f);
        minStatement.addResultColumn(vc);

        bc.addPart(minStatement);
        return result;
    }

    private SelectStatement newStatement(boolean pUseView) {
        SelectStatement st = sqlFactory.newSelectStatement();
        st.setTable(dbAkte);
        SelectTableReference akte = st.getSelectTableReference();
        akte.setAlias("a");

        //aktenzeichen joinen
        JoinReference az = akte.join(dbAktenzeichen);
        az.setAlias("az");
        CombinedConstraint onClause = az.getOn();
        BooleanConstraint bc = onClause.createEQ();
        bc.addPart(akte.newColumnReference(dbAkte.getColumn("aId")));
        bc.addPart(az.newColumnReference(dbAktenzeichen.getColumn("aAktenId")));
        bc = onClause.createEQ();
        bc.addPart(az.newColumnReference(dbAktenzeichen.getColumn("aFilter")));
        bc.addPart("Hauptverfahren");

        //beteiligte joinen
        JoinReference kl, be;
        if (pUseView) {
            kl = getFirstRowStatement2("Klaeger", akte, az, "kl");
            be = getFirstRowStatement2("Beklagter", akte, kl, "be");
        } else {
            kl = getFirstRowStatement("Klaeger", akte, az, "kl");
            be = getFirstRowStatement("Beklagter", akte, kl, "be");
            st.addResultColumn(getCountStatement("anzahlKlaeger", "Klaeger", akte, "klc"));
            st.addResultColumn(getCountStatement("anzahlBeklagte", "Beklagter", akte, "bec"));
        }
        JoinReference ber = be.leftOuterJoin(dbBeteiligte);
        ber.setAlias("ber");
        addAktenId(ber.getOn(), akte, ber);
        addEQ(ber, ber.getOn(), dbBeteiligte.getColumn("aFilter"), "Beklagter");

        return st;
    }

    /** <p>Creates a complex SELECT statement and runs the generator on it.</p>
     */
    public void testCreate1() {
        SelectStatement st = newStatement(false);
        String got = sqlGenerator.getQuery(st);
        String expect = "SELECT (SELECT COUNT(*) AS anzahlKlaeger FROM DBBeteiligte klc WHERE"
            + " (klc.aFilter='Klaeger' AND klc.aAktenId=a.aId)) AS anzahlKlaeger,"
            + " (SELECT COUNT(*) AS anzahlBeklagte FROM DBBeteiligte bec WHERE"
            + " (bec.aFilter='Beklagter' AND bec.aAktenId=a.aId)) AS anzahlBeklagte"
            + " FROM DBAkte a, DBAktenzeichen az, DBBeteiligte kl, DBBeteiligte be,"
            + " DBBeteiligte ber WHERE (a.aId=az.aAktenId AND az.aFilter='Hauptverfahren')"
            + " AND (kl.aAktenId(+)=a.aId AND kl.aFilter(+)='Klaeger' AND"
            + " UPPER(kl.aName(+))=(SELECT MIN(UPPER(klmin.aName)) AS MIN FROM"
            + " DBBeteiligte klmin WHERE (kl.aAktenId(+)=klmin.aAktenId AND"
            + " klmin.aFilter='Klaeger'))) AND (be.aAktenId(+)=a.aId AND"
            + " be.aFilter(+)='Beklagter' AND UPPER(be.aName(+))=(SELECT"
            + " MIN(UPPER(bemin.aName)) AS MIN FROM DBBeteiligte bemin WHERE"
            + " (be.aAktenId(+)=bemin.aAktenId AND bemin.aFilter='Beklagter'))) AND"
            + " (ber.aAktenId(+)=a.aId AND ber.aFilter(+)='Beklagter')";
        assertEquals(expect, got);
    }

    /** <p>Creates another complex SELECT statement and runs the generator on it.</p>
     */
    public void testCreate2() {
        SelectStatement st = newStatement(true);
        String got = sqlGenerator.getQuery(st);
        String expect = "SELECT * FROM DBAkte a, DBAktenzeichen az,"
            + " (SELECT DBBeteiligte.NUM, DBBeteiligte.aName, DBBeteiligte.aVorname,"
            + " DBBeteiligte.aAktenId FROM (SELECT COUNT(*) OVER (PARTITION BY aAktenId) AS NUM,"
            + " MIN(UPPER(aName)) OVER (PARTITION BY aAktenId) AS MINANAME,"
            + " MIN(UPPER(NVL(aVorname, ' '))) OVER (PARTITION BY aAktenId, UPPER(aName)) AS MINAVORNAME,"
            + " DBBeteiligte0.aName, DBBeteiligte0.aVorname, DBBeteiligte0.aAktenId FROM"
            + " DBBeteiligte DBBeteiligte0 WHERE DBBeteiligte0.aFilter='Klaeger') WHERE"
            + " (DBBeteiligte.MINAVORNAME=MIN() AND"
            + " DBBeteiligte.MINANAME=MIN(UPPER(DBBeteiligte.aName)))),"
            + " (SELECT DBBeteiligte1.NUM, DBBeteiligte1.aName, DBBeteiligte1.aVorname,"
            + " DBBeteiligte1.aAktenId FROM (SELECT COUNT(*) OVER (PARTITION BY aAktenId)"
            + " AS NUM, MIN(UPPER(aName)) OVER (PARTITION BY aAktenId) AS MINANAME,"
            + " MIN(UPPER(NVL(aVorname, ' '))) OVER (PARTITION BY aAktenId, UPPER(aName)) AS MINAVORNAME,"
            + " DBBeteiligte2.aName, DBBeteiligte2.aVorname, DBBeteiligte2.aAktenId"
            + " FROM DBBeteiligte DBBeteiligte2 WHERE DBBeteiligte2.aFilter='Beklagter')"
            + " DBBeteiligte1 WHERE (DBBeteiligte1.MINAVORNAME=MIN() AND"
            + " DBBeteiligte1.MINANAME=MIN(UPPER(DBBeteiligte1.aName)))),"
            + " DBBeteiligte ber WHERE (a.aId=az.aAktenId AND az.aFilter='Hauptverfahren')"
            + " AND a.aId=kl.aAktenId(+) AND a.aId=be.aAktenId(+) AND"
            + " (ber.aAktenId(+)=a.aId AND ber.aFilter(+)='Beklagter')";
        assertEquals(expect, got);
    }

    private void addEQ(TableReference pRef1, TableReference pRef2,
                       CombinedConstraint pConstraint,
                       Column pCol1, Column pCol2) {
        BooleanConstraint bc = pConstraint.createEQ();
        bc.addPart(pRef1.newColumnReference(pCol1));
        bc.addPart(pRef2.newColumnReference(pCol2));
    }

    private void addEQ(TableReference pRef1, CombinedConstraint pConstraint,
                       Column pCol1) {
    	BooleanConstraint bc = pConstraint.createEQ();
    	bc.addPart(pRef1.newColumnReference(pCol1));
    	bc.addPlaceholder();
    }

    /**
     * Returns the Statment
     * <pre> 
     *   SELECT KE.AID FROM KETTENELEMENT KE JOIN VERTRETREKETTE VK
     *     ON KE.AID = VK.WURZELELEMENT
     *     WHERE KE.ORGANISATIONSID = ? 
     * </pre>
     */
    private SelectStatement getSelectAidByOrganisationsId() {
        SelectStatement stmt = sqlFactory.newSelectStatement();
        stmt.setTable(kettenElement);
        SelectTableReference kettenElementRef = stmt.getSelectTableReference();
        JoinReference vertreterKetteRef = kettenElementRef.join(vertreterKette);
        addEQ(kettenElementRef, vertreterKetteRef, vertreterKetteRef.getOn(),
              kettenElement.getColumn("aId"),
              vertreterKette.getColumn("wurzelElement"));
        addEQ(kettenElementRef, stmt.getWhere(), kettenElement.getColumn("organisationsId"));
        stmt.addResultColumn(kettenElementRef.newColumnReference(kettenElement.getColumn("aId")));
        return stmt;
    }

    /**
     *  <p>Creates the statement
     * <pre>
     *    SELECT AID FROM KETTENELEMENT 
     *    START WITH KE.AID IN
     *    ( SELECT KE.AID FROM KETTENELEMENT KE JOIN VERTRETREKETTE VK
     *      ON KE.AID = VK.WURZELELEMENT
     *      WHERE KE.ORGANISATIONSID = ?
     *    )
     *    CONNECT BY PRIOR KE.AID=KE.AVERTRETERID 
     * </pre></p>
     */
    private SelectStatement getSelectAllChildsByOrganisationsId() {
        OraSelectStatement stmt = (OraSelectStatement) sqlFactory.newSelectStatement();
        stmt.setTable(kettenElement);
        TableReference tRef  = stmt.getTableReference();

        BooleanConstraint in = stmt.getStartWith().createIN();
        in.addPart(tRef.newColumnReference(kettenElement.getColumn("aId")));
        in.addPart(getSelectAidByOrganisationsId());
        
        BooleanConstraint bc = stmt.getConnectBy().createEQ();
        OraColumnReference ref1 = (OraColumnReference) tRef.newColumnReference(kettenElement.getColumn("aId"));
        ref1.setPrior(true);
        bc.addPart(ref1);
        bc.addPart(tRef.newColumnReference(kettenElement.getColumn("aVertreterId")));

        return stmt;
    }

    /** <p>Creates the statement
     * <pre>
     *    DELETE FROM KETTENELEMENT KE WHERE AID IN
     *   ( SELECT AID FROM KETTENELEMENT 
     *     START WITH KE.AID IN
     *     ( SELECT KE.AID FROM KETTENELEMENT KE JOIN VERTRETREKETTE VK
     *        ON KE.AID = VK.WURZELELEMENT
     *        WHERE KE.ORGANISATIONSID = ?
     *     )
     *     CONNECT BY PRIOR KE.AID=KE.AVERTRETERID
     *   )
     * </pre></p>
     */
    private DeleteStatement getDeleteAllChildsByOrganisationsId() {
        DeleteStatement dstmt = sqlFactory.newDeleteStatement();
        dstmt.setTable(kettenElement);
        TableReference tRef  = dstmt.getTableReference();
        CombinedConstraint whereClause = dstmt.getWhere();
        BooleanConstraint bc = whereClause.createIN();
        bc.addPart(tRef.newColumnReference(kettenElement.getColumn("aId")));
        bc.addPart(getSelectAllChildsByOrganisationsId());
        return dstmt;
    }

    /** <p>Creates the query
     * <pre>
     * </pre>
     * and verifies the generator results.</p>
     */
    public void testDelete1() {
    	DeleteStatement ds = getDeleteAllChildsByOrganisationsId();
        String expect = "DELETE FROM KettenElement WHERE KettenElement.aId IN" +
                        " ((SELECT * FROM KettenElement KettenElement0 START WITH KettenElement0.aId IN ((SELECT KettenElement1.aId FROM KettenElement KettenElement1, VertreterKette WHERE KettenElement1.aId=wurzelElement AND KettenElement1.organisationsId=?)) CONNECT BY PRIOR KettenElement0.aId=KettenElement0.aVertreterId))";
        String got = sqlGenerator.getQuery(ds);
        assertEquals(expect, got);
    }
}
