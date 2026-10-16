package ci553.happyshop.catalogue;

public class OrangisedTrolley extends Trolley {

    @Override
    public void addProduct(Product product){
        MergeAndSort.mergeProduct(getProducts(), product);
    }


}
