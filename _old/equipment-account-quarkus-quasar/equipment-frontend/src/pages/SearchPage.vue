<template>
  <!-- <q-page class="row items-center justify-evenly"> -->
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
    <!-- <q-dialog v-model="logDlgShowing" full-width>
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
    </q-dialog> -->

    <q-table
      @selection="selectionChanged"
      dense
      :rows="result"
      :columns="columns"
      :rows-per-page-options="[20, 50]"
      :loading="loading"
      :visible-columns="visibleColumns"
      v-model:pagination="pagination"
      row-key="guid"
      selection="multiple"
      v-model:selected="selected"
    >
      <template v-slot:body-cell-actions="props">
        <q-td :props="props">
          <q-btn-group dense>
            <q-btn
              color="primary"
              icon="edit"
              dense
              title="Редактировать"
              @click="editEquipment(props)"
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
      </template>
    </q-table>
    <span class="fixed-top-right q-mb-sm" style="z-index: 9999">
      <q-btn dense @click="showHelp" icon="help_outline" color="info" />
      <q-btn
        dense
        @click="openSearchDialog"
        icon="search"
        color="primary"
        v-shortkey="['ctrl', 'shift', 'f']"
        @shortkey="openSearchDialog"
      />
      <!-- <q-btn
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
      /> -->
    </span>
  </q-page>
</template>

<script lang="ts">
import { Criteria, SearchResult } from 'components/models';
import SearchDlg from 'components/SearchDlg.vue';
import { defineComponent, ref } from 'vue';
import { useGlobalStore } from 'stores/global-store';

function useSearch() {
  let criteria = ref<Criteria>({
    invNumber: 'inv1',
    personId: 0,
    stateId: 0,
    typeId: 0,
    serial: '',
    comment: '',
    purchaseDate: null,
    displayDate: null,
  });

  function search() {
    alert('Search pressed');
  }

  return { criteria, search };
}

function useResultTable() {
  interface SelectionChangedEvent {
    added: boolean;
    keys: string[];
  }
  const store = useGlobalStore();
  function selectionChanged(d: SelectionChangedEvent) {
    if (d.added) {
      store.selectGuids(d.keys);
    } else {
      store.unSelectGuids(d.keys);
    }
  }
  return { selectionChanged, selected: ref([]) };
}

function useToolbar() {
  const searchShowing = ref(false);
  function showHelp() {
    alert(
      'Ctrl+Shift+F для поиска\nCtrl+Shift+P для печати инвентарных номеров из буфера обмена'
    );
  }
  function openSearchDialog() {
    searchShowing.value = true;
  }
  return  { searchShowing, showHelp, openSearchDialog };
}

const visibleColumns = [
  'invNumber',
  'serial',
  'type',
  'state',
  'person',
  'comment',
  'purchaseDate',
  'actions',
];
const columns = [
  {
    name: 'invNumber',
    align: 'left',
    label: 'Инвентарник',
    field: 'invNumber',
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
    name: 'purchaseDate',
    align: 'left',
    label: 'Дата',
    field: 'purchaseDate',
    sortable: true,
  },
  {
    name: 'actions',
    align: 'left',
    label: 'Действия',
    sortable: false,
    field: 'guid',
  },
];

export default defineComponent({
  name: 'SearchPage',
  components: { SearchDlg },
  setup() {
    const guid = ref<string | null>(null);
    const result = ref<SearchResult[]>([
      {
        guid: 'guid1',
        invNumber: 'inv3',
        serial: 'serial3',
        type: 'notebook',
        state: 'new',
        person: 'ltv',
        comment: 'comment1',
        purchaseDate: null,
      },
      {
        guid: 'guid2',
        invNumber: 'inv2',
        serial: 'serial2',
        type: 'PC',
        state: 'used',
        person: 'kan',
        comment: 'comment2',
        purchaseDate: null,
      },
    ]);
    return {
      ...useSearch(),
      ...useResultTable(),
      ...useToolbar(),
      showBanner: ref(false),
      bannerMsg: ref('bannerMsg'),

      editShowing: ref(false),
      maintenanceDlgShowing: ref(false),
      logDlgShowing: ref(false),
      loading: ref(false),
      guid,
      result,
      equipment: ref(null),
      columns,
      visibleColumns,
      pagination: { rowsPerPage: 20 },
    };
  },
});
</script>
