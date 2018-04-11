package co.com.ies.fidelizacioncliente.entity;

/**
 * Item básico para objetos que solo requieran un nombre y un id
 */
public class GenericItem {

   private String id;
   private String name;

   public GenericItem (String id, String name) {

      this.id = id;
      this.name = name;
   }

   public String getId () {

      return id;
   }

   public void setId (String id) {

      this.id = id;
   }

   public String getName() {

      return name;
   }

   public void setName(String name) {

      this.name = name;
   }
}
