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
import org.jgrapht.traverse.DepthFirstIterator;

import it.polito.tdp.artsmia.db.ArtsmiaDAO;

public class Model {
	
	private Graph<ArtObject,DefaultWeightedEdge> grafo;
	private List<ArtObject> allNodes;
	private ArtsmiaDAO dao;
	private Map <Integer, ArtObject> idMap; //la idmap si costruisce sui vertici!
	
	
	public Model() {
		this.grafo=new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		this.allNodes=new ArrayList<>();
		this.dao=new ArtsmiaDAO();
		this.idMap=new HashMap<Integer,ArtObject>();
	}
	/**
	 * metodo che riepie il grafo:
	 * alternativa 1: molto lenta!!!!!
	 */
	
	public void buildGraph() {
		//carico nodi
		this.loadNodes();
		//aggiungo tutti i nodi al grafo
		Graphs.addAllVertices(this.grafo, this.allNodes);
		//devo adesso caricare gli archi
		
//		for (ArtObject o1: allNodes) {
//			for (ArtObject o2:allNodes) {
//				int peso=dao.getWeight(o1.getId(), o2.getId());
//				Graphs.addEdgeWithVertices(this.grafo, o1,o2,peso);
//			}
//		}
//		
		
		List<edgeModel> allEdges=this.dao.getAllWeights(idMap);
		for (edgeModel e:allEdges) {
			Graphs.addEdgeWithVertices(this.grafo, e.getSource(), e.getTarget(),e.getPeso());
		}
		
		System.out.println("This graph contains " + this.grafo.vertexSet().size()+" vertci e "+this.grafo.edgeSet().size());
	}
	
	
	private void loadNodes(){
		if(this.allNodes.isEmpty()) {
			this.allNodes=dao.listObjects();
			for (ArtObject o: this.allNodes) {
				this.idMap.put(o.getId(), o);
			}
		}
	}
	
	
	
	public boolean isIdInGraph(int objectId) {
		if (this.idMap.get(objectId)!=null) 
			return true;
		else
			return false;	
	}
	
	public Integer calcolaConnessa(int objectId) {
		//con un iteratore esploro il grafo                                                             //nodo sorgente
		DepthFirstIterator<ArtObject,DefaultWeightedEdge> iterator=new DepthFirstIterator<>(this.grafo, this.idMap.get(objectId));
		List<ArtObject> compConnessa=new ArrayList<>();
		while(iterator.hasNext()) {
			compConnessa.add(iterator.next());	
		}
		//alternativa:
		ConnectivityInspector<ArtObject,DefaultWeightedEdge> inspector=new ConnectivityInspector<>(this.grafo);
		Set <ArtObject>setConnesso=inspector.connectedSetOf(this.idMap.get(objectId));
		
		return compConnessa.size();
		//return setConnesso.size();
		// la dimensione della componente connessa è la quanttà di tutti i nodi connessi
		
	}
	
}
	

