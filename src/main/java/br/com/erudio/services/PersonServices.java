package br.com.erudio.services;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import br.com.erudio.controllers.PersonController;
import br.com.erudio.data.vo.v1.PersonVO;
import br.com.erudio.data.vo.v2.PersonVOV2;
import br.com.erudio.exceptions.RequiredObjectIsNullException;
import br.com.erudio.exceptions.ResourceNotFoundException;
import br.com.erudio.mapper.DozerMapper;
import br.com.erudio.mapper.custom.PersonMapper;
import br.com.erudio.model.Person;
import br.com.erudio.repositories.PersonRepository;
import jakarta.transaction.Transactional;

@Service
public class PersonServices {

//	private final AtomicLong counter = new AtomicLong();
	private Logger logger = Logger.getLogger(PersonServices.class.getName());
	
	@Autowired
	private PersonRepository repository;
	
	@Autowired
	private PersonMapper mapper;
	
	// Injetando um serviço que cria no HETOAS o link contendo orientação de qual página o item da lista se encontra.
	@Autowired
	private PagedResourcesAssembler<PersonVO> assembler;
	
	// Modificando o serviço para retornar um PagedModel<EntityModel<VO>>
	public PagedModel<EntityModel<PersonVO>> findAll(Pageable pageable) {
		logger.info("Finding all people!");
		
		//Armazenando o personPage e convertendo em VO
		var personPage = repository.findAll(pageable);
		var personVosPage = personPage.map(p -> DozerMapper.parseObject(p, PersonVO.class));
		
		//Acrescentando link do HETOAS em cada linha do personVO
		personVosPage.map(p -> p.add(linkTo(methodOn(PersonController.class).findById(p.getKey())).withSelfRel()));

		// Especificando o que será acrescido no link HETOAS
		Link link = linkTo(
				methodOn(PersonController.class) //Definindo a classe controller
				.findAll(pageable.getPageNumber(), pageable.getPageSize(), "asc") //Definindo o que será acrescido (numero da página, tamanho da página, ordenação
			).withSelfRel(); //Definindo como link
		
		//Retornando no formato PagedModel<EntityModel<VO>>, usando a o serviço injetado assembler.toModel
		return assembler.toModel(personVosPage, link);
	}
	
	
	public PagedModel<EntityModel<PersonVO>> findPersonByName(String firstName, Pageable pageable) {
		logger.info("Finding all people!");
		
		//Armazenando o personPage e convertendo em VO
		var personPage = repository.findPersonsByName(firstName, pageable);
		var personVosPage = personPage.map(p -> DozerMapper.parseObject(p, PersonVO.class));
		
		//Acrescentando link do HETOAS em cada linha do personVO
		personVosPage.map(p -> p.add(linkTo(methodOn(PersonController.class).findById(p.getKey())).withSelfRel()));
		
		// Especificando o que será acrescido no link HETOAS
		Link link = linkTo(
				methodOn(PersonController.class) //Definindo a classe controller
				.findAll(pageable.getPageNumber(), pageable.getPageSize(), "asc") //Definindo o que será acrescido (numero da página, tamanho da página, ordenação
				).withSelfRel(); //Definindo como link
		
		//Retornando no formato PagedModel<EntityModel<VO>>, usando a o serviço injetado assembler.toModel
		return assembler.toModel(personVosPage, link);
	}

	public PersonVO findById(Long id) {
		logger.info("Finding one person!");
		var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		PersonVO vo = DozerMapper.parseObject(entity, PersonVO.class);
		vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
		return vo;
	}
	
	public PersonVO create(PersonVO person) {
		if(person == null) throw new RequiredObjectIsNullException();
		logger.info("Create one person!");
		var entity = DozerMapper.parseObject(person, Person.class);
		var vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
		vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());
		return vo;
	}
	
	public PersonVOV2 createV2(PersonVOV2 person) {
		logger.info("Create one person with V2!");
		var entity = mapper.convertVOToEntity(person);
		var vo = mapper.convertEntityToVo(repository.save(entity));
		vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());
		return vo;
	}
	
	public PersonVO update(PersonVO person) {
		if(person == null) throw new RequiredObjectIsNullException();
		logger.info("Update one person!");
		var entity = repository.findById(person.getKey()).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));	
		entity.setFirstName(person.getFirstName());
		entity.setLastName(person.getLastName());
		entity.setAddress(person.getAddress());
		entity.setGender(person.getGender());
		var vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
		vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());
		return vo;
	}
	
	@Transactional
	public PersonVO disablePerson(Long id) {
		logger.info("Disabling one person!");
		repository.disablePerson(id);
		
		var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		PersonVO vo = DozerMapper.parseObject(entity, PersonVO.class);
		vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
		return vo;
	}
	
	public void delete(Long id) {
		logger.info("Delete one person!");
		var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		repository.delete(entity);
	}
}
