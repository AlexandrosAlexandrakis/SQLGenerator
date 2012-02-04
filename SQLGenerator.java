/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graphtorelation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SQLGenerator {

    private static final String NEWLINE = System.getProperty("line.separator");
    static String sql = "";
    static Map<String, Integer> idDewMap = new HashMap<String, Integer>();
    private static int ID = 0;

    private Node n;
    private String dewey_code;
    private String name;
    private String parentName="";
    private String attributeNames = "";
    private String attributeValues = "";
    private String parent_dewey_code = "";


    public SQLGenerator(Node nn, String code) {
        ID++;
        n = nn;
        name = nn.getNodeName();
        setAttributes();
        SetDeweyCode(code);
        setParentDew();
        parentName=nn.getParentNode().getNodeName();
        idDewMap.put(dewey_code, ID);
        displayNode();
        sqlCreation();
        createRTN();
    }
	//BFS
    public void parseTheXMLFromTheRoot() {


        Queue<SQLGenerator> q = new LinkedList<SQLGenerator>();
        q.add(this);

        while (!q.isEmpty()) {
            SQLGenerator currentNode = q.poll();
            if (currentNode.n.hasChildNodes()) {
                NodeList LL = currentNode.n.getChildNodes();
                int j = 0;
                for (int i = 0; i < LL.getLength(); i++) {
                    if (LL.item(i).getNodeType() == Node.ELEMENT_NODE) {
                        j++;
                        SQLGenerator newNode = new SQLGenerator(LL.item(i), currentNode.dewey_code + "." + j);
                        q.add(newNode);
                    }
                }
            }
        }
    }

    private void sqlCreation() {

        sql += "INSERT INTO " + name + " (NODE_ID,DEWEY_CODE,";
        if ("".equals(parentName)) {
            sql += "#document";
        } else {
            sql += parentName + "_FK ,";
        }
        sql += attributeNames;
        sql += "DOC_ID)";
        sql += NEWLINE;
        sql += "VALUES (";
        sql += ID + ",";
        sql += '\"' + dewey_code + '\"' + ",";
        sql += idDewMap.get(parent_dewey_code) + ",";
        sql += attributeValues;
        sql += "1)";
        sql += NEWLINE;
        sql += NEWLINE;

    }

    private void SetDeweyCode(String d_c) {
        dewey_code = d_c;
    }

    private void setAttributes() {
        NamedNodeMap NNM = n.getAttributes();
        if (NNM != null) {
            for (int k = 0; k < NNM.getLength(); k++) {
                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    attributeNames += "a_" + NNM.item(k).getNodeName() + ",";
                    attributeValues += '\"' + NNM.item(k).getTextContent() + '\"' + ",";
                }
            }
        }
    }

    private void setParentDew() {
        String dewCode = dewey_code;

        int strLen = dewCode.length();
        if (strLen < 2) {
            parent_dewey_code = "";
        } else {
            int lastIdx = strLen - 1;
            parent_dewey_code = dewCode.substring(0, lastIdx);
            char last = dewCode.charAt(lastIdx);
            while (last != '.') {
                lastIdx = lastIdx - 1;
                parent_dewey_code = dewCode.substring(0, lastIdx);
                last = dewCode.charAt(lastIdx);
            }
        }
    }

    private void displayNode() {
        System.out.println(name);
        System.out.println("-Node ID = " + ID);
        System.out.println("-Dewey Code = " + dewey_code);
        if (!attributeNames.equals("")) {
            System.out.println("-Attribute Names = " + attributeNames);
        }
        if (!attributeValues.equals("")) {
            System.out.println("-Attribute Values = " + attributeValues);
        }
        System.out.println("-Parent Dewey Code = " + parent_dewey_code);
    }
}
