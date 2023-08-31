<template>
  <div>
    <q-card>
      <q-card-section class="row items-center">
        <div class="text-h6">{{ m.inv_number }}</div>
        <q-space />
        <q-btn icon="close" flat round dense v-close-popup />
      </q-card-section>

      <q-card-section class="items-center" :disabled="initialError">
        <q-input outlined v-model="m.guid" label="GUID" dense disable />
        <q-input outlined v-model="m.serial" label="Серийник" dense />
        <q-input outlined v-model="m.comment" label="Комментарий" dense />

        <q-input outlined v-model="displayDate" label="Дата покупки" dense>
          <template v-slot:append>
            <q-icon name="event" class="cursor-pointer">
              <q-popup-proxy
                ref="qDateProxy"
                transition-show="scale"
                transition-hide="scale"
              >
                <q-date
                  v-model="displayDate"
                  @input="() => $refs.qDateProxy.hide()"
                  mask="DD.MM.YYYY"
                  :locale="locale"
                />
              </q-popup-proxy>
            </q-icon>
          </template>
        </q-input>

        <q-select
          outlined
          dense
          v-model="m.type"
          hide-selected
          fill-input
          :options="typesFiltered"
          emit-value
          map-options
          option-value="label"
          use-input
          @filter="filterTypes"
          label="Тип"
        >
          <template v-slot:append>
            <q-icon
              name="close"
              @click="m.type = null"
              class="cursor-pointer"
              v-if="m.type"
            />
          </template>
        </q-select>

        <q-select
          outlined
          dense
          v-model="m.state"
          hide-selected
          fill-input
          :options="statesFiltered"
          emit-value
          map-options
          option-value="label"
          use-input
          @filter="filterStates"
          label="Состояние"
        >
          <template v-slot:append>
            <q-icon
              name="close"
              @click="m.state = null"
              class="cursor-pointer"
              v-if="m.state"
            />
          </template>
        </q-select>

        <q-select
          outlined
          dense
          v-model="m.person"
          hide-selected
          fill-input
          :options="personsFiltered"
          emit-value
          map-options
          option-value="label"
          use-input
          @filter="filterPersons"
          label="Ответственный"
        >
          <template v-slot:append>
            <q-icon
              name="close"
              @click="m.person = null"
              class="cursor-pointer"
              v-if="m.person"
            />
          </template>
        </q-select>
      </q-card-section>

      <q-card-actions align="right">
        <q-btn icon="close" label="Отмена" color="primary" @click="close" />
        <q-btn
          icon="save"
          color="primary"
          @click="save"
          :disable="$v.m.$invalid"
        />
      </q-card-actions>
    </q-card>
  </div>
</template>

<script>
import { locale } from 'src/lib/consts'
import { regex } from 'src/lib/validators'
import {
  dateDisplayToIso,
  dateIsoToDisplay,
  filterQSelect,
} from 'src/lib/funcs'
import {
  required,
  maxLength,
  minLength,
  minValue,
} from 'vuelidate/lib/validators'
import { mapGetters } from 'vuex'
import { ajax } from 'src/lib/api'
export default {
  data() {
    return {
      typesFiltered: [],
      statesFiltered: [],
      personsFiltered: [],
      locale,
      initialError: true,
      displayDate: null,
      m: {
        guid: null,
        inv_number: '',
        person_id: 0,
        state_id: 0,
        type_id: 0,
        serial: '',
        comment: '',
        purchase_date: null,
      },
    }
  },
  methods: {
    close() {
      this.$emit('close')
    },
    save() {
      let vm = this
      let m = vm.m
      m.purchase_date = dateDisplayToIso(vm.displayDate)
      debugger
      ajax.updateEqDescr(m, m).then(
        r => vm.$emit('close', r.data.content),
        err => vm.$q.dialog({ title: 'Error', message: err, persistent: true })
      )
      vm.$emit('close')
    },
    filterTypes(val, update, abort) {
      filterQSelect(this, val, update, this.eqTypes, 'typesFiltered')
    },
    filterStates(val, update, abort) {
      filterQSelect(this, val, update, this.eqStates, 'statesFiltered')
    },
    filterPersons(val, update, abort) {
      filterQSelect(this, val, update, this.persons, 'personsFiltered')
    },
  },
  props: ['guid'],
  computed: {
    ...mapGetters(['eqTypes', 'eqStates', 'persons']),
  },
  validations() {
    return {
      displayDate: { regex: regex(/^\d{2}\.\d{2}\.\d{4}$/) },
      m: {
        guid: { required, maxLength: maxLength(36), minLength: minLength(36) },
        inv_number: { required },
        person_id: { required, minValue: minValue(1) },
        state_id: { required, minValue: minValue(1) },
        type_id: { required, minValue: minValue(1) },
      },
    }
  },
  mounted() {
    let vm = this
    vm.typesFiltered = vm.eqTypes
    vm.statesFiltered = vm.eqStates
    vm.personsFiltered = vm.persons
    ajax.getEquipment({ guid: vm.guid }).then(
      r => {
        vm.initialError = false
        vm.m = r.data.content
        vm.displayDate = dateIsoToDisplay(vm.m.purchase_date)
      },
      err => {
        vm.$log.error('error ', err)
      }
    )
  },
}
</script>

<style></style>
