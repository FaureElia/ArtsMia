package it.polito.tdp.artsmia.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.artsmia.model.ArtObject;
import it.polito.tdp.artsmia.model.edgeModel;

public class ArtsmiaDAO {

	public List<ArtObject> listObjects() {
		
		String sql = "SELECT * from objects";
		List<ArtObject> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				ArtObject artObj = new ArtObject(res.getInt("object_id"), res.getString("classification"), res.getString("continent"), 
						res.getString("country"), res.getInt("curator_approved"), res.getString("dated"), res.getString("department"), 
						res.getString("medium"), res.getString("nationality"), res.getString("object_name"), res.getInt("restricted"), 
						res.getString("rights_type"), res.getString("role"), res.getString("room"), res.getString("style"), res.getString("title"));
				
				result.add(artObj);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public Integer getWeight(int sourceId,int targetId) {
		String sql="SELECT e1.object_id AS o1,  e2.object_id AS o2, COUNT(*) AS peso "
				+ "FROM exhibition_objects e1, exhibition_objects e2 "
				+ "WHERE  e1.exhibition_id=e2.exhibition_id AND e1.object_id=? AND e2.object_id=?";
		
		Connection conn=DBConnect.getConnection();
		try {
			PreparedStatement st=conn.prepareStatement(sql);
			st.setInt(1, sourceId);
			st.setInt(2, targetId);
			ResultSet rs=st.executeQuery();
			//mi sposto sul primo
			rs.next();
			//il peso corrisponde al numero di volte in cui le coppie compaiono nella stessa exhibition
			int peso=rs.getInt("peso");
			
		    conn.close();
		    return peso;
		}catch(SQLException e) {
			e.printStackTrace();
			System.out.println("Error in dao");
			return null;
		}
	}
	/**
	 * Metodo 2 più veloce!!
	 * cerco le coppie all'interno del database e per ogni coppia conto il numero di volte che si verifica!!
	 * metto un > per evitare che la coppia si ripeta in senso opposto!
	 * in questo modo con una singola interrogazione riesco ad ottenere tutti gli archi presenti nel grafo!
	 * NOTA: se una coppia non è collegata, ovvero non compare MAI in na stessa esibizione, essa non comparirà
	 * perchè non è soddisfatta la condizione nel WHERE.
	 * Evito di creare ogni volta un nuovo nodo, definendo una mappa id che mi permette di accedere alle informazioni di tale nodo
	 * @param mappaId
	 * @return
	 */
	
	public List<edgeModel> getAllWeights(Map<Integer,ArtObject> mappaId) {
		String sql="SELECT e1.object_id AS o1,  e2.object_id AS o2, COUNT(*) AS peso "
				+ "FROM exhibition_objects e1, exhibition_objects e2 "
				+ "WHERE  e1.exhibition_id=e2.exhibition_id AND e1.object_id> e2.object_id "
				+ "GROUP by e1.object_id, e2.object_id "
				+ "ORDER BY peso DESC ";
		Connection conn=DBConnect.getConnection();
		// definisco una lista di edgeModel, che mi rappresenta tutti gli archi presenti all'interno del grafo
		List<edgeModel> allEdges=new ArrayList<>();
		try {
			PreparedStatement st=conn.prepareStatement(sql);
			ResultSet rs=st.executeQuery();
			while (rs.next()) {
				int idSource=rs.getInt( "o1");
				int idTarget=rs.getInt( "o2");
				int peso=rs.getInt("peso");
				edgeModel e=new edgeModel(mappaId.get(idSource),mappaId.get(idTarget),peso);
				allEdges.add(e);
				
			}
			
			conn.close();
			return allEdges;
		    
		}catch(SQLException e) {
			System.out.println("Error in dao");
			return null;
		}
		
	}
	
}
