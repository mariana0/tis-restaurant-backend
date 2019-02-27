package tis.server;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Pedido {
	// Mesa mesa;
	// Item item;
	int mesa;
	int id;		
	int item;
	String obs;
	int status;
	String hora;
	ObjectNode meNode;

	Pedido(int mesa, int item, String o) {

		this.mesa = mesa;
		this.status = 0;
		this.obs = o;
		this.item = item;
		LocalDateTime hora = LocalDateTime.now();
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
//		this.hora=hora.format(formatter);
		this.hora=hora.toString();

	}
	
	public ObjectNode toObjectNode() {
		JsonNodeFactory factory = JsonNodeFactory.instance;
		ObjectNode pedido = factory.objectNode();
		pedido.put("mesa", this.mesa);
		pedido.put("id", this.id);
		pedido.put("item", this.item);
		pedido.put("obs", this.obs);
		pedido.put("status", this.status);
		pedido.put("hora", this.hora);
		this.meNode=pedido;
		return pedido;		
	}

	public void setStatus(int x) {
		status=x;
	}

	public int getMesa() {
		return mesa;
	}

	public int getItem() {
		return item;
	}

	public int getStatus() {
		return status;
	}

	public String getObs() {
		return obs;
	}

	public String getHora() {
		return hora;
	}

	public int getId() {
		return id;
	}

}
