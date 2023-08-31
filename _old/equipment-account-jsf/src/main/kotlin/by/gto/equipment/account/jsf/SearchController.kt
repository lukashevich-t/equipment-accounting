package by.gto.equipment.account.jsf

import by.gto.equipment.account.model.Equipment
import by.gto.equipment.account.model.EquipmentDescr
import by.gto.equipment.account.service.ServiceImpl
import org.primefaces.PrimeFaces
import org.primefaces.event.SelectEvent
import java.io.Serializable
import javax.annotation.PostConstruct
import javax.inject.Named
import javax.enterprise.context.RequestScoped
import javax.enterprise.context.SessionScoped
import javax.inject.Inject

@Named("searchController")
@SessionScoped
open class SearchController: Serializable {
    @Inject
    private lateinit var service: ServiceImpl

    var searchResult = listOf<EquipmentDescr>()
    var searchCriteria = { -> val x = Equipment(); x.invNumber = "480699";x }()

    open fun showSearchDlg() {
        val current = PrimeFaces.current()
        val dialog = current.dialog()
        dialog.openDynamic("/searchDlg.xhtml", mapOf("resizeable" to true), null);
    }

    open fun search(event: SelectEvent) {
//        service.searchEquipment(searchCriteria)
        println(event)
    }

    open fun submitSearch() {
        PrimeFaces.current().dialog().closeDynamic(searchCriteria)
    }

}