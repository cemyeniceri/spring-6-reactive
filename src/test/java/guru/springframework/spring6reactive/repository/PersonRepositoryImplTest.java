package guru.springframework.spring6reactive.repository;

import guru.springframework.spring6reactive.domain.Person;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PersonRepositoryImplTest {

    PersonRepositoryImpl personRepository = new PersonRepositoryImpl();

    @Test
    void testGetByIdBlock() {
        Mono<Person> personMono = personRepository.getById(1);
        Person person = personMono.block();
        assertEquals("Cem", person.getFirstName());
    }

    @Test
    void testGetByIdStepVerifier() {
        Mono<Person> personMono = personRepository.getById(1);
        StepVerifier.create(personMono).expectNextCount(1).verifyComplete();
        personMono.subscribe(person -> System.out.println(person.getFirstName()));
    }

    @Test
    void testGetByIdEmptyMono() {
        Mono<Person> personMono = personRepository.getById(100);
        assertEquals(Mono.empty(), personMono);
    }

    @Test
    void testGetByIdEmptyMonoStepVerifier() {
        Mono<Person> personMono = personRepository.getById(100);
        StepVerifier.create(personMono).expectNextCount(0).verifyComplete();
        personMono.subscribe(person -> System.out.println(person.getFirstName()));
    }

    @Test
    void testGetByIdSubscribe() {
        Mono<Person> personMono = personRepository.getById(1);
        personMono.subscribe(person -> System.out.println(person.getFirstName()));
    }

    @Test
    void testMonoMapOperation() {
        Mono<Person> personMono = personRepository.getById(1);
        personMono.map(Person::getFirstName).subscribe(System.out::println);
    }

    @Test
    void testFluxBlock() {
        Flux<Person> personFlux = personRepository.findAll();
        Person person = personFlux.blockFirst();
        System.out.println(person.getFirstName());
    }

    @Test
    void testFluxSubscribe() {
        Flux<Person> personFlux = personRepository.findAll();
        personFlux.subscribe(person -> System.out.println(person.getFirstName()));
    }

    @Test
    void testFluxMapOperation() {
        Flux<Person> personFlux = personRepository.findAll();
        personFlux.map(Person::getFirstName).subscribe(System.out::println);
    }

    @Test
    void testFluxToList() {
        Flux<Person> personFlux = personRepository.findAll();
        Mono<List<Person>> personlistMono = personFlux.collectList();
        personlistMono.subscribe(persons -> persons.forEach(person -> System.out.println(person.getFirstName())));
    }

    @Test
    void testFluxFilterOnName() {
        Flux<Person> personFlux = personRepository.findAll();
        personFlux
                .filter(person -> person.getFirstName().equals("Ares"))
                .subscribe(person -> System.out.println(person.getFirstName()));
    }

    @Test
    void testFluxFilterById() {
        Flux<Person> personFlux = personRepository.findAll();
        Mono<Person> secondMono = personFlux
                .filter(person -> person.getId() == 2).next();
        secondMono
                .subscribe(person -> System.out.println(person.getFirstName()));
    }

    @Test
    void testFindPersonByIdNotFound() {
        Flux<Person> personFlux = personRepository.findAll();

        final Integer id = 100;

        Mono<Person> single = personFlux.filter(person -> person.getId().equals(id))
                .single().doOnError(throwable -> System.out.println("Person not found in the flux"));

        single.subscribe(
                person -> System.out.println(person.getFirstName()),
                throwable -> System.out.println("Person not found in the mono")
        );
    }
}