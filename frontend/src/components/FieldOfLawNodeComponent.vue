<script lang="ts" setup>
import { computed, toRaw } from "vue"
import TokenizeText from "@/components/TokenizeText.vue"
import { ROOT_ID, FieldOfLawNode } from "@/domain/fieldOfLaw"
import FieldOfLawService from "@/services/fieldOfLawService"

interface Props {
  selectedNodes: FieldOfLawNode[]
  node: FieldOfLawNode
  selected: boolean
  showNorms: boolean
}

const props = defineProps<Props>()
const emit = defineEmits<{
  "node:toggle": [node: FieldOfLawNode]
  "node:select": [node: FieldOfLawNode]
  "node:unselect": [identifier: string]
  "linkedField:clicked": [identifier: string]
}>()

const node = computed(() => props.node)

function handleTokenClick(tokenContent: string) {
  emit("linkedField:clicked", tokenContent)
}

function canLoadMoreChildren() {
  return node.value.childrenCount > node.value.children.length
}

async function handleToggle() {
  if (canLoadMoreChildren()) {
    let childToReattach: FieldOfLawNode
    if (node.value.children.length > 0) {
      // can only happen if inDirectPathMode
      childToReattach = toRaw(node.value.children[0])
    }
    await FieldOfLawService.getChildrenOf(node.value.identifier).then(
      (response) => {
        if (!response.data) return
        node.value.children = response.data
        if (!childToReattach) return
        const parentToReattachTo = node.value.children.find(
          (node) => node.identifier === childToReattach.identifier,
        )
        if (!parentToReattachTo) return
        parentToReattachTo.children = childToReattach.children
        parentToReattachTo.isExpanded = true
        parentToReattachTo.inDirectPathMode = true
      },
    )
  }
  if (node.value.inDirectPathMode) {
    node.value.inDirectPathMode = false
  } else {
    node.value.isExpanded = !node.value.isExpanded
  }
}
</script>

<template>
  <div
    class="flex flex-col"
    :class="node.identifier !== ROOT_ID ? 'pl-36' : ''"
  >
    <div class="flex flex-row">
      <div v-if="node.childrenCount === 0" class="pl-24"></div>
      <div v-else>
        <button
          :aria-label="node.identifier + ' ' + node.text + ' aufklappen'"
          class="material-icons w-icon rounded-full bg-blue-200 text-blue-800"
          @click="handleToggle"
        >
          {{
            canLoadMoreChildren() || !props.node.isExpanded ? "add" : "remove"
          }}
        </button>
      </div>
      <div v-if="node.identifier !== ROOT_ID">
        <button
          :aria-label="
            node.identifier +
            ' ' +
            node.text +
            (selected ? ' entfernen' : ' hinzufügen')
          "
          class="ml-12 h-24 w-24 appearance-none rounded-sm border-2 align-top text-blue-800 outline-none outline-0 outline-offset-[-4px] outline-blue-800 hover:outline-2 focus:outline-2"
          @click="
            selected
              ? emit('node:unselect', node.identifier)
              : emit('node:select', node)
          "
        >
          <span
            v-if="selected"
            aria-label="Sachgebiet entfernen"
            class="material-icons selected-icon"
          >
            done
          </span>
        </button>
      </div>
      <div>
        <div class="flex flex-col">
          <div class="flex flex-row">
            <div v-if="node.identifier !== ROOT_ID" class="identifier pl-8">
              {{ node.identifier }}
            </div>
            <div class="font-size-14px pl-6 pt-2 text-blue-800">
              <TokenizeText
                :keywords="props.node.linkedFields ?? []"
                :text="props.node.text"
                @link-token:clicked="handleTokenClick"
              />
            </div>
          </div>
        </div>
        <div v-if="showNorms" class="flex flex-col pb-6 pl-8">
          <div class="font-size-14px norms-font-color flex flex-row flex-wrap">
            <span v-for="(norm, idx) in node.norms" :key="idx">
              <strong>{{ norm.singleNormDescription }}</strong>
              {{ norm.abbreviation
              }}{{ idx < node.norms.length - 1 ? ",&nbsp;" : "" }}
            </span>
          </div>
        </div>
      </div>
    </div>
    <div v-if="node.isExpanded && node.children.length">
      <FieldOfLawNodeComponent
        v-for="child in node.children"
        :key="child.identifier"
        :node="child"
        :selected="
          props.selectedNodes.some(
            ({ identifier }) => identifier === child.identifier,
          )
        "
        :selected-nodes="selectedNodes"
        :show-norms="showNorms"
        @linked-field:clicked="emit('linkedField:clicked', $event)"
        @node:select="emit('node:select', $event)"
        @node:unselect="emit('node:unselect', $event)"
      />
    </div>
  </div>
</template>

<style lang="scss" scoped>
.identifier {
  font-size: 16px;
  white-space: nowrap;
}

// TODO use tailwind instead
.font-size-14px {
  font-size: 14px;
}

.selected-icon {
  font-size: 20px;
}

// will be integrated into the styleguide
.norms-font-color {
  color: #66522e;
}
</style>
