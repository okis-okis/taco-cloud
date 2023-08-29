package tacos.web;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import lombok.extern.slf4j.Slf4j;
import tacos.Ingredient;
import tacos.Ingredient.Type;
import tacos.Taco;
import tacos.TacoOrder;
import tacos.data.IngredientRepository;

@Slf4j
@Controller
@RequestMapping("/design")
@SessionAttributes("tacoOrder")
public class DesignTacoController {
	
	private final IngredientRepository ingredientRepo;
	
	@Autowired
	public DesignTacoController(
			IngredientRepository ingredientRepo) {
		this.ingredientRepo = ingredientRepo;
	}
	
	@ModelAttribute
	public void addIngredientsToModel(Model model) {
		Iterable<Ingredient> ingredients = ingredientRepo.findAll();
		
		Type[] types = Ingredient.Type.values();
		
		List<Ingredient> listIngredients = 
				  StreamSupport.stream(ingredients.spliterator(), false)
				    .collect(Collectors.toList());
			
		for(Type type : types) {
			model.addAttribute(type.toString().toLowerCase(),
					filterByType(listIngredients, type));
		}
	}
	
	@ModelAttribute(name = "tacoOrder")
	public TacoOrder order() {
		return new TacoOrder();
	}
	
	@ModelAttribute(name = "taco")
	public Taco taco() {
		return new Taco();
	}
	
	@GetMapping
	public String showDesignForm() {
		return "design";
	}
	
	public Iterable<Ingredient> filterByType(
		List<Ingredient> ingradients, Type type){
		return ingradients.stream()
				.filter(x -> x.getType().equals(type))
				.collect(Collectors.toList());
	}
	
	@PostMapping
	public String processTaco(
			@Valid Taco taco, Errors errors,
			@ModelAttribute TacoOrder tacoOrder) {
		if(errors.hasErrors()) {
			return "design";
		}
		
		tacoOrder.addTaco(taco);
		log.info("Processing taco: {}", taco);
		
		return "redirect:/orders/current";
	}
}
