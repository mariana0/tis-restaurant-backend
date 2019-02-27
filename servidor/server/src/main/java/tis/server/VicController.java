package tis.server;

import java.util.concurrent.atomic.AtomicLong;
import java.util.ArrayList;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;


@RestController
public class VicController {
	private final AtomicLong counter = new AtomicLong();
	ArrayList<Mesa> abertas = new ArrayList<Mesa>();
	ArrayList<Mesa> fechadas = new ArrayList<Mesa>();
	JsonNodeFactory factory = JsonNodeFactory.instance;

//@RequestMapping (value="/", method=RequestMethod.GET)
	
	@RequestMapping(value = "/mesas/{idmesa}", method = RequestMethod.DELETE)
	public ObjectNode fecMesa(@PathVariable("idmesa") int idmesa) {
		
		ObjectNode header = factory.objectNode();
		ObjectNode content = factory.objectNode();
		ObjectNode resposta = factory.objectNode();

		Mesa procurada = new Mesa (0,0);
		for (Mesa m:abertas) {
			if (m.id==idmesa) {		
				procurada=m;
			}
		}	
				
		if (procurada.numero!=0) {
			procurada.fechar();
			fechadas.add(procurada);
			abertas.remove(procurada);
			header.put("status", 2);
			header.put("status_desc", "Ok");
			content.put("mesa", procurada.toObjectNode());
			resposta.put("header", header);
			resposta.put("content", content);
		} else {
			header.put("status", 4);
			header.put("status_desc", "Erro do Cliente");
			content.put("descricao", "Mesa número " + idmesa + " não está aberta ou não existe");
			resposta.put("header", header);
			resposta.put("content", content);
		}
		return resposta;		
		
	}	
	
	@RequestMapping(value = "/mesas", method = RequestMethod.POST)
	public ObjectNode addMesa(@RequestBody ObjectNode o) {

		JsonNode conteudo=o.get("content");
		JsonNode idmesa = conteudo.get("mesa");		
		ObjectNode header = factory.objectNode();
		ObjectNode content = factory.objectNode();
		ObjectNode resposta = factory.objectNode();

		if (!o.has("content")||!conteudo.has("mesa")) {
			header.put("status", 4);
			header.put("status_desc", "Erro do Cliente");
			content.put("descricao", "Json recebido está incompleto - deve ter o objeto 'mesa' dentro de 'content'.");
			resposta.put("header", header);
			resposta.put("content", content);
			return resposta;
		}

		for (Mesa mesa:abertas) {
			if (mesa.numero==idmesa.asInt()) {
				header.put("status", 4);
				header.put("status_desc", "Erro do Cliente");
				content.put("descricao", "Mesa de número "+idmesa.asInt()+" já está aberta.");
				resposta.put("header", header);
				resposta.put("content", content);
				return resposta;
			}
		}
		
		Mesa m = new Mesa(idmesa.asInt(), (int) counter.incrementAndGet());
		abertas.add(m);
		header.put("status", 2);
		header.put("status_desc", "Ok");
		content.put("mesa", m.toObjectNode());
		resposta.put("header", header);
		resposta.put("content", content);

		return resposta;

	}

	@RequestMapping(value = "/mesas/{idmesa}", method = RequestMethod.POST)
	public ObjectNode addPedido(@PathVariable("idmesa") int idmesa, @RequestBody ObjectNode o) {

		ObjectNode header = factory.objectNode();
		ObjectNode content = factory.objectNode();
		ObjectNode resposta = factory.objectNode();
		
		JsonNode conteudo = o.get("content");
		JsonNode item = conteudo.get("item");
		JsonNode obs = conteudo.get("obs");

		if (!o.has("content")||!conteudo.has("item") || !conteudo.has("obs")) {
			header.put("status", 4);
			header.put("status_desc", "Erro do Cliente");
			content.put("descricao", "Json recebido está incompleto - deve ter os objetos 'item' e 'obs' dentro de 'content'.");
			resposta.put("header", header);
			resposta.put("content", content);
			return resposta;
		}

		Mesa procurada = new Mesa(0, 0);

		for (Mesa m : abertas) {
			if (m.numero == idmesa) {
				procurada = m;
			}
		}

		if (procurada.numero != 0) {
			String observacao = obs.asText();
			Pedido p = new Pedido(procurada.numero, item.asInt(), observacao);
			procurada.addPedido(p);
			header.put("status", 2);
			header.put("status_desc", "Ok");
			content.put("pedido", p.toObjectNode());
			resposta.put("header", header);
			resposta.put("content", content);

		} else {
			header.put("status", 4);
			header.put("status_desc", "Erro do Cliente");
			content.put("descricao", "Mesa número " + idmesa + " não está aberta ou não existe");
			resposta.put("header", header);
			resposta.put("content", content);
		}
		return resposta;
	}

	@RequestMapping(value = "/mesas", method = RequestMethod.GET)
	public ObjectNode listarMesas() {

		ObjectNode header = factory.objectNode();
		ObjectNode content = factory.objectNode();
		ObjectNode resposta = factory.objectNode();

		ArrayNode mesas = factory.arrayNode();
		for (Mesa m : abertas) {
			mesas.add(m.toObjectNode());
		}

		header.put("status", 2);
		header.put("status_desc", "Ok");
		content.put("mesas", mesas);
		resposta.put("header", header);
		resposta.put("content", content);

		return resposta;
	}

	@RequestMapping(value = "/mesas/{idmesa}", method = RequestMethod.GET)
	public ObjectNode verMesa(@PathVariable("idmesa") int idmesa) {

		ObjectNode header = factory.objectNode();
		ObjectNode content = factory.objectNode();
		ObjectNode resposta = factory.objectNode();

		Mesa procurada = new Mesa(0, 0);

		for (Mesa m : abertas) {
			if (m.numero == idmesa) {
				procurada = m;
			}
		}

		if (procurada.numero != 0) {
			header.put("status", 2);
			header.put("status_desc", "Ok");
			content.put("mesa", procurada.toObjectNode());
			resposta.put("header", header);
			resposta.put("content", content);

		} else {
			header.put("status", 4);
			header.put("status_desc", "Erro do Cliente");
			content.put("descricao", "Mesa número " + idmesa + " não está aberta ou não existe");
			resposta.put("header", header);
			resposta.put("content", content);
		}

		return resposta;

	}

	@RequestMapping(value = "/mesas/{idmesa}/pedidos/{idpedido}", method = RequestMethod.PUT)
	public ObjectNode alterarPedido(@PathVariable("idmesa") int idmesa, @PathVariable("idpedido") int idpedido,
			@RequestBody ObjectNode o) {

		ObjectNode header = factory.objectNode();
		ObjectNode content = factory.objectNode();
		ObjectNode resposta = factory.objectNode();
		
		JsonNode conteudo = o.get("content");
		JsonNode status = conteudo.get("status");
		JsonNode obs = conteudo.get("obs");

		if (!conteudo.has("status")||!o.has("content")) {
			header.put("status", 4);
			header.put("status_desc", "Erro do Cliente");
			content.put("descricao", "Json recebido está incompleto - deve ter o objeto 'status' dentro de 'content'.");
			resposta.put("header", header);
			resposta.put("content", content);
			return resposta;
		}
		
		boolean encontrada = false;
		boolean encontrado = false;
		Pedido procurado= new Pedido (0,0,"");

		for (Mesa m : abertas) {
			if (m.numero == idmesa) {
				encontrada = true;
				for (Pedido p : m.pedidos) {
					if (p.id == idpedido) {
						encontrado = true;
						p.setStatus(status.asInt());
						procurado = p;
						if (conteudo.hasNonNull("obs")) {
							p.obs += (" " + obs.asText());
						}
					}
				}
			}
		}

		if (!encontrada || !encontrado) {
			header.put("status", 4);
			header.put("status_desc", "Erro do Cliente");
			content.put("descricao", "Mesa " + idmesa + " foi encontrada? " + encontrada + 
					" | Pedido " + idpedido + " foi encontrado? " + encontrado);
			resposta.put("header", header);
			resposta.put("content", content);
		}

		if (encontrada&&encontrado&&procurado.status==status.asInt()) {
			header.put("status", 2);
			header.put("status_desc", "Ok");
			content.put("pedido", procurado.toObjectNode());
			resposta.put("header", header);
			resposta.put("content", content);
		}
		
		return resposta;

	}

}