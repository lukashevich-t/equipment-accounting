<template>
  <div>
    <q-card>
      <q-card-section class="row items-center">
        <div class="text-h6">
          {{ equipment.inv_number }} {{ equipment.type }}
        </div>
        <q-space />
        <q-btn icon="close" flat round dense v-close-popup />
      </q-card-section>

      <q-card-section class="items-center">
        <q-select
          outlined
          dense
          v-model="m.actionId"
          hide-selected
          fill-input
          :options="actionsFiltered"
          emit-value
          map-options
          option-value="id"
          use-input
          @filter="filterActions"
          label="Состояние"
        >
          <template v-slot:append>
            <q-icon
              name="close"
              @click="m.actionId = null"
              class="cursor-pointer"
              v-if="m.actionId"
            />
          </template>
        </q-select>
        <q-input v-model="m.comment" filled type="textarea" />
      </q-card-section>

      <q-card-actions align="right">
        <q-btn icon="close" label="Отмена" color="primary" @click="close" />
        <q-btn
          icon="save"
          color="primary"
          @click="save"
          label="Сохранить"
          :disable="$v.m.$invalid"
        />
      </q-card-actions>
    </q-card>
  </div>
</template>

<script>
import { required } from 'vuelidate/lib/validators'
import { mapGetters } from 'vuex'
import {
  // dateDisplayToIso,
  // dateIsoToDisplay,
  filterQSelect,
} from 'src/lib/funcs'
import { showError } from 'src/lib/mixins'
import { ajax } from 'src/lib/api'
export default {
  data() {
    return {
      actionsFiltered: [],
      m: {
        guid: null,
        actionId: 3,
        userId: 0,
        time: '1900-01-01T00:00:00',
        comment: null,
      },
    }
  },
  validations() {
    return {
      m: {
        guid: { required },
        actionId: { required },
        comment: { required },
      },
    }
  },
  props: {
    guid: { type: String, required: true },
    equipment: { type: Object, required: true },
  },
  methods: {
    save() {
      let m = this.m
      debugger
      // m.time = dateDisplayToIso(m.time)
      let vm = this
      vm.blockUI = true
      ajax
        .addLog(m, m)
        .then(
          r => {
            vm.$log.debug('success')
            vm.$emit('close')
            // vm.$dlg.toast('OK', { messageType: 'info', closeTime: 3 })
          },
          err => {
            vm.$showError('Ошибка', err)
          }
        )
        .finally(() => {
          vm.blockUI = false
        })
    },
    filterActions(val, update, abort) {
      filterQSelect(this, val, update, this.actions, 'actionsFiltered')
    },
    close() {
      this.$emit('close')
    },
  },
  mounted() {
    this.m.guid = this.guid
    this.actionsFiltered = this.actions
  },
  computed: {
    ...mapGetters(['actions']),
  },
  mixins: [showError],
}
</script>
