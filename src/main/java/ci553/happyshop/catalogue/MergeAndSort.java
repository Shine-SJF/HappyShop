package ci553.happyshop.catalogue;

import java.util.ArrayList;

public class MergeAndSort {

    public static void mergeProduct(ArrayList<Product> Trolley, Product newProduct) {

        boolean found = false;

        for (Product p : Trolley) {
            if (p.getProductId().equals(newProduct.getProductId())) {
                p.setOrderedQuantity(p.getOrderedQuantity() + newProduct.getOrderedQuantity());
                found = true;
                break;
            }

            }
        if (!found) {
            // add a copy instead of the original reference
            Product copyOfProduct = new Product(
                    newProduct.getProductId(),
                    newProduct.getProductDescription(),
                    newProduct.getProductImageName(),
                    newProduct.getUnitPrice(),
                    newProduct.getOrderedQuantity(),
                    newProduct.getStockQuantity());
            Trolley.add(copyOfProduct);
        }
        }
    }
