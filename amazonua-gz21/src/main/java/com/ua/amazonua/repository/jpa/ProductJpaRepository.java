package com.ua.amazonua.repository.jpa;

import com.ua.amazonua.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA репозиторій для {@link Product}.
 *
 * ШАР: repository/jpa. Успадкування від {@link JpaRepository} дає готові методи
 * (findAll, findById, save, deleteById, deleteAll...) — реалізацію генерує Spring Data.
 * Метод findByCategoryIgnoreCase — derived query: Spring будує SQL за назвою методу.
 *
 * Це НЕ наш доменний інтерфейс {@code ProductRepository}. Його поєднує з доменним
 * контрактом адаптер {@code JpaProductRepositoryAdapter} — так зберігається DIP.
 */
public interface ProductJpaRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryIgnoreCase(String category);
}
