package academy.ucuenca.store.shoppingservice.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import academy.ucuenca.store.shoppingservice.Entity.Invoice;
import academy.ucuenca.store.shoppingservice.Entity.InvoiceItem;
import academy.ucuenca.store.shoppingservice.Repository.InvoiceItemsRepository;
import academy.ucuenca.store.shoppingservice.Repository.InvoiceRepository;
import academy.ucuenca.store.shoppingservice.client.CustomerClient;
import academy.ucuenca.store.shoppingservice.client.ProductClient;
import academy.ucuenca.store.shoppingservice.model.Customer;
import academy.ucuenca.store.shoppingservice.model.Product;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    InvoiceRepository invoiceRepository;

    @Autowired
    InvoiceItemsRepository invoiceItemsRepository;

    @Autowired
    CustomerClient customerClient;

    @Autowired
    ProductClient productClient;

    @Override
    public List<Invoice> findInvoiceAll() {
        // TODO Auto-generated method stub
        return  invoiceRepository.findAll();
    }

    @Override
    public Invoice createInvoice(Invoice invoice) {
        Invoice invoiceDB = invoiceRepository.findByNumberInvoice ( invoice.getNumberInvoice () );
        if (invoiceDB !=null){
            return  invoiceDB;
        }
        invoice.setState("CREATED");
        invoiceDB=invoiceRepository.save(invoice);

        invoiceDB.getItems().forEach(invoiceItem->{//actualizar el stock de cada producto despues de la venta
            productClient.updateStock(invoiceItem.getProductId(), invoiceItem.getQuantity()*-1);
        });

        return invoiceRepository.save(invoice);
    }


    @Override
    public Invoice updateInvoice(Invoice invoice) {
        Invoice invoiceDB = getInvoice(invoice.getId());
        if (invoiceDB == null){
            return  null;
        }
        invoiceDB.setCustomerId(invoice.getCustomerId());
        invoiceDB.setDescription(invoice.getDescription());
        invoiceDB.setNumberInvoice(invoice.getNumberInvoice());
        invoiceDB.getItems().clear();
        invoiceDB.setItems(invoice.getItems());
        return invoiceRepository.save(invoiceDB);
    }


    @Override
    public Invoice deleteInvoice(Invoice invoice) {
        Invoice invoiceDB = getInvoice(invoice.getId());
        if (invoiceDB == null){
            return  null;
        }
        invoiceDB.setState("DELETED");
        return invoiceRepository.save(invoiceDB);
    }

    @Override
    public Invoice getInvoice(Long id) {

        Invoice invoice=invoiceRepository.findById(id).orElse(null);
        if(invoice!=null){
            Customer customer=customerClient.getCustomer(invoice.getCustomerId()).getBody();
            invoice.setCustomer(customer);
            List<InvoiceItem> listItem=invoice.getItems().stream().map(item->{
                Product product = productClient.getProduct(item.getProductId()).getBody();
                item.setProduct(product);
                return item;
            }).collect(Collectors.toList());
            invoice.setItems(listItem);
        }
        return invoice;
    }
   
   
}