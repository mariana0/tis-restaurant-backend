package tis.server;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class Mesa {
	int id;
	int numero;
	String abertura;
	String fechamento;
	float total;
	int formaPgto;
	ArrayList<Pedido> pedidos;
	ObjectNode meNode;

	Mesa(int numero, int id) {
		this.numero = numero;
		this.id = id;
		LocalDateTime abertura = LocalDateTime.now();
		//DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		//this.abertura = abertura.format(formatter);
		this.abertura=abertura.toString();
		total = 0.0F;
		pedidos = new ArrayList<Pedido>();
	}

	void addPedido(Pedido p) {
		pedidos.add(p);
		p.id = pedidos.size();
	}
	
	void fechar () {
		LocalDateTime fechamento = LocalDateTime.now();
		this.fechamento=fechamento.toString();
	}

	public ObjectNode toObjectNode() {

		JsonNodeFactory factory = JsonNodeFactory.instance;

		ArrayNode pedidos = factory.arrayNode();
		int contador = 0;
		for (Pedido p : this.pedidos) {
			pedidos.insert(contador, p.toObjectNode());
			contador++;
		}

		ObjectNode mesa = factory.objectNode();
		mesa.put("numero", this.numero);
		mesa.put("id", this.id);
		mesa.put("abertura", this.abertura);
		mesa.put("fechamento", this.fechamento);
		mesa.put("total", this.total);
		mesa.put("formaPgto", this.formaPgto);
		mesa.put("pedidos", pedidos);

		this.meNode = mesa;
		return mesa;

	}

	public int getId() {
		return id;
	}

	public int getNumero() {
		return numero;
	}

	public int getFormaPgto() {
		return formaPgto;
	}

	public String getAbertura() {
		return abertura;
	}

	public String getFechamento() {
		return fechamento;
	}

	public float getTotal() {
		return total;
	}

	public ArrayList<Pedido> getPedidos() {
		return pedidos;
	}

}
