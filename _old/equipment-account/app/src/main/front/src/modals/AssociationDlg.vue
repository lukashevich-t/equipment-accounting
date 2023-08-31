<template>
  <div>
    <q-card>
      <q-card-section class="row items-center">
        <div class="text-h6">Критерии объединения</div>
        <q-space />
        <q-btn icon="close" flat round dense v-close-popup />
      </q-card-section>

      <q-card-section class="items-center">
        <template>
          <div class="q-pa-md">
            <q-option-group
              :options="[
                { label: 'Ответственный', value: 'person' },
                { label: 'Тип', value: 'type' },
              ]"
              type="radio"
              v-model="radiobut"
            />
          </div>
        </template>
        <div v-show="radiobut == 'type'">
          <q-select
            outlined
            dense
            v-model="criteria.type_id"
            hide-selected
            fill-input
            :options="typesFiltered"
            emit-value
            map-options
            use-input
            @filter="filterTypes"
            label="Тип1"
          >
            <template v-slot:append>
              <q-icon
                name="close"
                @click="criteria.type_id = 0"
                class="cursor-pointer"
                v-if="criteria.type_id"
              />
            </template>
          </q-select>
          <q-select
            outlined
            dense
            v-model="AssociationCrit.type_id"
            hide-selected
            fill-input
            :options="typesFiltered"
            emit-value
            map-options
            use-input
            @filter="filterTypes"
            label="Тип2"
          >
            <template v-slot:append>
              <q-icon
                name="close"
                @click="AssociationCrit.type_id = 0"
                class="cursor-pointer"
                v-if="AssociationCrit.type_id"
              />
            </template>
          </q-select>
        </div>
        <div v-show="radiobut == 'person'">
          <q-select
            outlined
            dense
            v-model="criteria.person_id"
            hide-selected
            fill-input
            :options="personsFiltered"
            emit-value
            map-options
            use-input
            @filter="filterPersons"
            label="Ответственный1"
          >
            <template v-slot:append>
              <q-icon
                name="close"
                @click="criteria.person_id = 0"
                class="cursor-pointer"
                v-if="criteria.person_id"
              />
            </template>
          </q-select>
          <q-select
            outlined
            dense
            v-model="AssociationCrit.person_id"
            hide-selected
            fill-input
            :options="personsFiltered"
            emit-value
            map-options
            use-input
            @filter="filterPersons"
            label="Ответственный2"
          >
            <template v-slot:append>
              <q-icon
                name="close"
                @click="AssociationCrit.person_id = 0"
                class="cursor-pointer"
                v-if="AssociationCrit.person_id"
              />
            </template>
          </q-select>
        </div>
      </q-card-section>

      <q-card-actions align="right">
        <!-- <q-btn flat label="test" color="primary" @click="$log.debug(criteria)"/> -->
        <q-btn flat label="Отмена" color="primary" v-close-popup />
        <q-btn flat label="Очистить" @click="clear" />
        <q-btn
          icon="move_to_inbox"
          color="primary"
          @click="Association"
          :disable="!containsCriteria || $v.criteria.$invalid"
        />
      </q-card-actions>
    </q-card>
  </div>
</template>

<script>
import { locale } from 'src/lib/consts'
import { regex } from 'src/lib/validators'
import { mapGetters } from 'vuex'
import { filterQSelect } from 'src/lib/funcs'
export default {
  data() {
    return {
      typesFiltered: [],
      personsFiltered: [],
      locale,
      radiobut: 'person',
    }
  },
  validations() {
    return {
      criteria: {
        displayDate: { regex: regex(/^\d{2}\.\d{2}\.\d{4}$/) },
      },
    }
  },
  methods: {
    Association() {
      let vm = this
      if (!vm.containsCriteria || vm.$v.criteria.$invalid) return
      this.$emit('Association', vm.criteria, vm.AssociationCrit)
    },
    clear() {
      let c = this.criteria
      c.inv_number = ''
      c.serial = ''
      c.displayDate = null
      c.purchase_date = null
      c.person_id = 0
      c.state_id = 0
      c.type_id = 0
      c.comment = ''
      let d = this.AssociationCrit
      d.person_id = 0
      d.type_id = 0
    },
    filterTypes(val, update, abort) {
      filterQSelect(this, val, update, this.eqTypes, 'typesFiltered')
    },
    filterPersons(val, update, abort) {
      filterQSelect(this, val, update, this.persons, 'personsFiltered')
    },
  },
  props: ['criteria', 'AssociationCrit'],
  computed: {
    ...mapGetters(['eqTypes', 'persons']),
    containsCriteria() {
      let crit = this.criteria
      for (let k in crit) {
        if (crit[k]) {
          return true
        }
      }
      return false
    },
  },
  mounted() {
    let vm = this
    vm.typesFiltered = vm.eqTypes
    vm.personsFiltered = vm.persons
    vm.$refs.invNumberInput.focus()
  },
}
</script>
