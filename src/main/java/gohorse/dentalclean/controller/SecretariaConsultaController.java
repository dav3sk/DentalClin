package gohorse.dentalclean.controller;

import gohorse.dentalclean.model.entity.Secretaria;
import gohorse.dentalclean.controller.util.JsfUtil;
import gohorse.dentalclean.controller.util.JsfUtil.PersistAction;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("secretariaConsulta")
@SessionScoped
public class SecretariaConsultaController implements Serializable {

    @EJB
    private gohorse.dentalclean.controller.SecretariaFacade ejbFacade;
    private List<Secretaria> items = null;
    private Secretaria selected;
    private boolean editable;

    public SecretariaConsultaController() {
        selected = new Secretaria();
        editable = false;
    }

    public Secretaria getSelected() {
        return selected;
    }

    public void setSelected(Secretaria selected) {
        System.out.println("sctr-[set]selected_"+selected.getNome());
        this.selected = selected;
    }
    
    public boolean isEditable() {
        return editable;
    }
    
    public boolean isSelectedNull() {
        return selected == null;
    }

    public void setEditable() {
        if(editable == false) {
            editable = true;
        }
        else {
            editable = false;
        }
        
        System.out.println("Edição:" + editable);
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private SecretariaFacade getFacade() {
        return ejbFacade;
    }

    public Secretaria prepareCreate() {
        selected = new Secretaria();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/secretaria").getString("SecretariaCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
        
        selected = new Secretaria();
    }

    public void update() {
        System.out.println("sctr-[update]selected_" + selected);
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/secretaria").getString("SecretariaUpdated"));
        
        selected = null;
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/secretaria").getString("SecretariaDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
        selected = null;
    }

    public List<Secretaria> getItems() {
        items = getFacade().findAll();
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/secretaria").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/secretaria").getString("PersistenceErrorOccured"));
            }
        }
    }

    public Secretaria getSecretaria(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Secretaria> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Secretaria> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Secretaria.class)
    public static class SecretariaControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            SecretariaConsultaController controller = (SecretariaConsultaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "secretariaController");
            return controller.getSecretaria(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Secretaria) {
                Secretaria o = (Secretaria) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Secretaria.class.getName()});
                return null;
            }
        }
    }
}
