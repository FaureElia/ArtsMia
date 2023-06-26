package it.polito.tdp.artsmia.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;

import it.polito.tdp.artsmia.db.ArtsmiaDAO;

public class Model {
	
	private Graph<ArtObject,DefaultWeightedEdge> grafo;
	private ArtsmiaDAO dao;
	private Map<Integer, ArtObject> idMap;
	private List<ArtObject> componenteConnessa;
	
	public Model() {
		this.dao=new ArtsmiaDAO();
	}
	public void creaGrafo() {
		this.idMap=new HashMap<Integer, ArtObject>();
		this.grafo=new SimpleWeightedGraph<ArtObject,DefaultWeightedEdge>(DefaultWeightedEdge.class);
		List<ArtObject> vertici=dao.listObjects();
		Graphs.addAllVertices(this.grafo, vertici);
		for(ArtObject a: vertici) {
			this.idMap.put(a.getId(),a);
		}
		List<edgeModel> archi=this.dao.getAllWeights(idMap);
		for(edgeModel e: archi) {
			DefaultWeightedEdge edge=this.grafo.addEdge(e.getSource(), e.getTarget());
			this.grafo.setEdgeWeight(edge,e.getPeso());
		}
	}
	
	
	public int getVertex() {
		return this.grafo.vertexSet().size();
		
	}
	
	public int getEdges() {
		return this.grafo.edgeSet().size();
		
	}
	public Set<ArtObject> componenteConnessa(int input) {
		if(this.idMap.get(input)==null) {
			return null;
		}
		//metodo 1
		System.out.println("provo metodo 1");
		BreadthFirstIterator<ArtObject,DefaultWeightedEdge> iteratore=new BreadthFirstIterator<>(this.grafo,this.idMap.get(input));
		List componenteConnessa=new ArrayList<>();
		while (iteratore.hasNext()) {
			componenteConnessa.add(iteratore.next());
		}
		
		//metodo 1b
		System.out.println("provo metodo 1b");
		DepthFirstIterator<ArtObject,DefaultWeightedEdge> iterator=new DepthFirstIterator<>(this.grafo,this.idMap.get(input));
		componenteConnessa=new ArrayList<>();
		while (iterator.hasNext()) {
			componenteConnessa.add(iterator.next());
		}
		
		
		//metodo 2
		System.out.println("provo metodo 2");
		componenteConnessa=new ArrayList<>();
		ConnectivityInspector<ArtObject,DefaultWeightedEdge> inspector=new ConnectivityInspector<>(this.grafo);
		Set<ArtObject> a=inspector.connectedSetOf(this.idMap.get(input));
		
		System.out.println("trovato");
		return a;
		
	}
	
	
}
	
	
	
	

