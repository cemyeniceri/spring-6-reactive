package guru.springframework.spring6reactive.repository;

import guru.springframework.spring6reactive.domain.Person;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Stream;

public class PersonRepositoryImpl implements PersonRepository {

    Person cem = Person.builder().id(1).firstName("Cem").lastName("Yeniceri").build();
    Person ruya = Person.builder().id(2).firstName("Ruya").lastName("Yeniceri").build();
    Person ares = Person.builder().id(3).firstName("Ares").lastName("Yeniceri").build();

    @Override
    public Mono<Person> getById(Integer id) {
        return Stream.of(cem, ruya, ares)
                .filter(person -> person.getId().equals(id)).findFirst()
                .map(Mono::just)
                .orElseGet(Mono::empty);
    }

    @Override
    public Flux<Person> findAll() {
        return Flux.just(cem, ruya, ares);

    }
}
