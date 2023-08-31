<template>
  <q-page>
    <q-banner inline-actions class="text-white bg-red" v-if="showBanner">
      {{ bannerMsg }}
      <template v-slot:action>
        <q-btn flat color="white" label="закрыть" @click="showBanner = false" />
      </template>
    </q-banner>
    <q-dialog v-model="searchShowing">
      <search-dlg @search="search" :criteria="criteria" />
    </q-dialog>
    <q-dialog v-model="logDlgShowing" full-width>
      <log-dlg
        :guid="guid"
        :equipment="equipment"
        @close="logDlgShowing = false"
      />
    </q-dialog>
    <q-dialog v-model="maintenanceDlgShowing" persistent full-width>
      <add-maintenance-dlg
        :guid="guid"
        :equipment="equipment"
        @close="maintenanceDlgShowing = false"
      />
    </q-dialog>
    <q-dialog v-model="editShowing" persistent>
      <edit-dlg :guid="guid" @close="editShowing = false" />
    </q-dialog>
    <q-table
      @selection="selectionChanged"
      dense
      :data="result"
      :columns="columns"
      :rows-per-page-options="[20, 50]"
      :loading="loading"
      :visible-columns="visibleColumns"
      :pagination.sync="pagination"
      row-key="guid"
      selection="multiple"
      :selected.sync="selected"
    >
      <!-- <q-tr>
        <q-td key="guid">c</q-td>
      </!-->
      <template v-slot:body="props">
        <q-tr :props="props">
          <q-td>
            <q-checkbox dense v-model="props.selected" />
          </q-td>
          <q-td key="inv_number" :props="props">{{
            props.row.inv_number
          }}</q-td>
          <q-td key="serial" :props="props">{{ props.row.serial }}</q-td>
          <q-td key="type" :props="props">{{ props.row.type }}</q-td>
          <q-td key="state" :props="props">{{ props.row.state }}</q-td>
          <q-td key="person" :props="props">
            {{ props.row.person }}
            <!-- <q-popup-edit v-model="props.row.person">
              <q-input v-model="props.row.person" dense autofocus counter />
            </q-popup-edit>-->
          </q-td>
          <q-td key="comment" :props="props">{{ props.row.comment }}</q-td>
          <q-td key="purchase_date" :props="props">{{
            props.row.purchase_date
          }}</q-td>
          <q-td key="guid">
            <q-btn-group dense>
              <q-btn
                color="primary"
                icon="edit"
                dense
                title="Редактировать"
                @click="edit(props)"
              />
              <q-btn
                color="accent"
                icon="date_range"
                dense
                title="История"
                @click="showLogDlg(props)"
              />
              <q-btn
                color="accent"
                icon="note_add"
                dense
                title="Обслуживание"
                @click="showMaintenanceDlg(props)"
              />
            </q-btn-group>
          </q-td>
        </q-tr>
      </template>
    </q-table>
    <span class="fixed-top-right q-mb-sm" style="z-index: 9999;">
      <q-btn dense @click="help" icon="help_outline" color="info" />
      <q-btn
        dense
        @click="openSearchDialog"
        icon="search"
        color="primary"
        v-shortkey="['ctrl', 'shift', 'f']"
        @shortkey="openSearchDialog"
      />
      <q-btn
        dense
        icon="print"
        :disabled="selectedGuids.length === 0"
        @click="print"
        title="Печатать инвентарники из буфера"
        v-shortkey="['ctrl', 'shift', 'p']"
        @shortkey="print"
        >{{ selectedGuids.length }}</q-btn
      >
      <q-btn
        dense
        icon="clear"
        :disabled="selectedGuids.length === 0"
        @click="_clearSelectedGuids"
        title="Очистить буфер инвентарников"
      />
    </span>
    <form
      method="post"
      action="s/eq/print_invs2"
      target="_blank"
      onsubmit="return false"
      ref="printForm"
    >
      <input type="hidden" name="ids_for_printing" ref="idsForPrinting" />
    </form>
  </q-page>
</template>

<script>
import SearchDlg from 'src/modals/SearchDlg'
import EditDlg from 'src/modals/EditDlg'
import LogDlg from 'src/modals/LogDlg'
import AddMaintenanceDlg from 'src/modals/AddMaintenanceDlg'
import { dateDisplayToIso } from 'src/lib/funcs'
import { ajax } from 'src/lib/api'
import { mapActions, mapGetters } from 'vuex'
export default {
  data() {
    return {
      guid: null,
      equipment: null,
      showBanner: false,
      bannerMsg: 'banner',
      pagination: { rowsPerPage: 20 },
      result: [],
      selected: [],
      visibleColumns: [
        'inv_number',
        'serial',
        'type',
        'state',
        'person',
        'comment',
        'purchase_date',
        'actions',
      ],
      columns: [
        {
          name: 'guid',
          field: 'guid',
          visible: false,
        },
        {
          name: 'inv_number',
          align: 'left',
          label: 'Инвентарник',
          field: 'inv_number',
          sortable: true,
        },
        {
          name: 'serial',
          align: 'left',
          label: 'Серийник',
          field: 'serial',
          sortable: true,
        },
        {
          name: 'type',
          align: 'left',
          label: 'Тип',
          field: 'type',
          sortable: true,
        },
        {
          name: 'state',
          align: 'left',
          label: 'Состояние',
          field: 'state',
          sortable: true,
        },
        {
          name: 'person',
          align: 'left',
          label: 'Ответственный',
          field: 'person',
          sortable: true,
        },
        {
          name: 'comment',
          align: 'left',
          label: 'Комментарий',
          field: 'comment',
          sortable: true,
        },
        {
          name: 'purchase_date',
          align: 'left',
          label: 'Дата',
          field: 'purchase_date',
          sortable: true,
        },
        {
          name: 'actions',
          align: 'left',
          label: 'Действия',
          sortable: false,
          field: 'guid',
        },
      ],
      searchShowing: true,
      editShowing: false,
      maintenanceDlgShowing: false,
      logDlgShowing: false,
      loading: false,
      criteria: {
        inv_number: '',
        person_id: 0,
        state_id: 0,
        type_id: 0,
        serial: '',
        comment: '',
        purchase_date: null,
        displayDate: null,
      },
    }
  },
  components: { SearchDlg, EditDlg, AddMaintenanceDlg, LogDlg },
  computed: { ...mapGetters(['selectedGuids']) },
  methods: {
    help() {
      alert(
        'Ctrl+Shift+F для поиска\nCtrl+Shift+P для печати инвентарных номеров из буфера обмена'
      )
    },
    openSearchDialog() {
      this.searchShowing = true
    },
    showLogDlg(props) {
      let vm = this
      vm.guid = props.row.guid
      vm.equipment = props.row
      vm.$nextTick(() => {
        vm.logDlgShowing = true
      })
    },
    print() {
      let vm = this
      if (vm.selectedGuids.length === 0) return
      vm.$refs.idsForPrinting.value = vm.selectedGuids.join(',')
      vm.$refs.printForm.submit()
    },
    ...mapActions(['selectGuids', 'unselectGuids', 'clearSelectedGuids']),
    _clearSelectedGuids() {
      this.clearSelectedGuids()
      this.selected = []
    },
    selectionChanged(d) {
      if (d.added) {
        this.selectGuids(d.keys)
      } else {
        this.unselectGuids(d.keys)
      }
      // this.$log.debug(this.selectedGuids)
    },
    edit(props) {
      let vm = this
      this.guid = props.row.guid
      this.$nextTick(() => {
        vm.editShowing = true
      })
    },
    showMaintenanceDlg(props) {
      let vm = this
      vm.guid = props.row.guid
      vm.equipment = props.row
      vm.$nextTick(() => {
        vm.maintenanceDlgShowing = true
      })
    },
    search(c) {
      c.purchase_date = dateDisplayToIso(c.displayDate)
      let vm = this
      vm.searchShowing = false
      vm.loading = true
      vm.criteria = c
      vm.result = []
      vm.selected = []
      ajax
        .searchEq(vm.criteria)
        .then(
          r => {
            vm.result = r.data.content
            for (let i in vm.result) {
              let obj = vm.result[i]
              if (vm.selectedGuids.indexOf(obj.guid) !== -1) {
                vm.selected.push(obj)
              }
            }
          },
          err =>
            vm.$q.dialog({ title: 'Error', message: err, persistent: true })
        )
        .finally(() => {
          vm.loading = false
        })
    },
  },
}
</script>

<style></style>
