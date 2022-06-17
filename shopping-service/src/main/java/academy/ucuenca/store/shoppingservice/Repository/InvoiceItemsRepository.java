package academy.ucuenca.store.shoppingservice.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import academy.ucuenca.store.shoppingservice.Entity.InvoiceItem;

public interface InvoiceItemsRepository extends JpaRepository<InvoiceItem,Long> {
}