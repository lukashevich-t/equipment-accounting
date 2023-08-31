
import javax.faces.context.FacesContext
import javax.faces.application.FacesMessage
import org.primefaces.model.menu.MenuModel
//import javax.swing.text.StyleConstants.setIcon
import org.primefaces.model.menu.DefaultMenuItem
import org.primefaces.model.menu.DefaultSubMenu
import org.primefaces.model.menu.DefaultMenuModel
import javax.annotation.PostConstruct
import javax.faces.bean.ManagedBean
//import javax.swing.text.StyleConstants.setIcon

@ManagedBean
class MenuView {

    var model: MenuModel? = null
        private set

    @PostConstruct
    fun init() {
        model = DefaultMenuModel()

        //First submenu
        val firstSubmenu = DefaultSubMenu("Dynamic Submenu")

        var item = DefaultMenuItem("External")
        item.url = "http://www.primefaces.org"
        item.icon = "pi pi-home"
        firstSubmenu.addElement(item)

        model!!.addElement(firstSubmenu)

        //Second submenu
        val secondSubmenu = DefaultSubMenu("Dynamic Actions")

        item = DefaultMenuItem("Save")
        item.icon = "pi pi-save"
        item.command = "#{menuView.save}"
        item.update = "messages"
        secondSubmenu.addElement(item)

        item = DefaultMenuItem("Delete")
        item.icon = "pi pi-times"
        item.command = "#{menuView.delete}"
        item.isAjax = false
        secondSubmenu.addElement(item)

        item = DefaultMenuItem("Redirect")
        item.icon = "pi pi-search"
        item.command = "#{menuView.redirect}"
        secondSubmenu.addElement(item)

        model!!.addElement(secondSubmenu)
    }

    fun save() {
        addMessage("Success", "Data saved")
    }

    fun update() {
        addMessage("Success", "Data updated")
    }

    fun delete() {
        addMessage("Success", "Data deleted")
    }

    fun addMessage(summary: String, detail: String) {
        val message = FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail)
        FacesContext.getCurrentInstance().addMessage(null, message)
    }
}