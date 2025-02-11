<script lang="ts" setup>
import { reactive } from "vue"
import { defineDateField } from "@/fields/caselaw"
import KitchensinkPage from "@/kitchensink/components/KitchensinkPage.vue"
import KitchensinkStory from "@/kitchensink/components/KitchensinkStory.vue"
import { InfoStatus } from "@/shared/components/enumInfoStatus"
import InfoModal from "@/shared/components/InfoModal.vue"
import DateInput from "@/shared/components/input/DateInput.vue"
import InputField from "@/shared/components/input/InputField.vue"
import NestedInput from "@/shared/components/input/NestedInput.vue"
import TextInput from "@/shared/components/input/TextInput.vue"
import TimeInput from "@/shared/components/input/TimeInput.vue"
import { NestedInputAttributes } from "@/shared/components/input/types"
import YearInput from "@/shared/components/input/YearInput.vue"

const nestedInputFields: NestedInputAttributes["fields"] = {
  parent: defineDateField("field", "Input", "Input"),
  child: defineDateField(
    "deviatingField",
    "Abweichender Input",
    "Abweichender Input",
  ),
}

const values = reactive({
  regularTextInput: "",
  textInputWithPlaceholder: "",
  textInputWithValue: "Hello world",
  invalidTextInput: "",
  errorMessageTextInput: "Invalid value",
  readonlyTextInput: "Read-only",
  mediumTextInput: "",
  smallTextInput: "",
  dateInput: "",
  yearInput: "",
  timeInput: "",
})
</script>

<template>
  <KitchensinkPage name="Input">
    <InfoModal
      description="In order to ensure a consistent layout for inputs, the actual input field must always be wrapped with an InputField containing the label and (if necessary) the error message."
      :status="InfoStatus.INFO"
      title="Code convention for usage of inputs"
    />
    <KitchensinkStory class="w-320" name="Regular">
      <InputField
        id="regularTextInput"
        v-slot="{ id }"
        label="Regular text input"
      >
        <TextInput
          :id="id"
          v-model="values.regularTextInput"
          aria-label="regular text input"
        />
      </InputField>
    </KitchensinkStory>

    <KitchensinkStory class="w-320" name="With placeholder">
      <InputField
        id="textInputWithPlaceholder"
        v-slot="{ id }"
        label="Regular text input"
      >
        <TextInput
          :id="id"
          v-model="values.textInputWithPlaceholder"
          aria-label="text input with placeholder"
          placeholder="Placeholder"
        />
      </InputField>
    </KitchensinkStory>

    <KitchensinkStory class="w-320" name="With value">
      <InputField
        id="textInputWithValue"
        v-slot="{ id }"
        label="Regular text input"
      >
        <TextInput
          :id="id"
          v-model="values.textInputWithValue"
          aria-label="text input with value"
        />
      </InputField>
    </KitchensinkStory>

    <KitchensinkStory class="w-320" name="Invalid">
      <InputField
        id="invalidTextInput"
        v-slot="{ id }"
        label="Regular text input"
      >
        <TextInput
          :id="id"
          v-model="values.invalidTextInput"
          aria-label="invalid text input"
          has-error
        />
      </InputField>
    </KitchensinkStory>

    <KitchensinkStory class="w-320" name="With error message">
      <InputField
        id="errorMessageTextInput"
        v-slot="{ id, hasError }"
        label="This has an error message"
        :validation-error="{
          defaultMessage: 'Error message',
          field: 'errorMessageTextInput',
        }"
      >
        <TextInput
          :id="id"
          v-model="values.errorMessageTextInput"
          aria-label="invalid text input"
          :has-error="hasError"
        />
      </InputField>
    </KitchensinkStory>

    <KitchensinkStory class="w-320" name="Read-only">
      <InputField
        id="readonlyTextInput"
        v-slot="{ id }"
        label="Regular text input"
      >
        <TextInput
          :id="id"
          v-model="values.readonlyTextInput"
          aria-label="readonly text input"
          read-only
        />
      </InputField>
    </KitchensinkStory>

    <KitchensinkStory class="w-320" name="Medium">
      <InputField
        id="mediumTextInput"
        v-slot="{ id }"
        label="Regular text input"
      >
        <TextInput
          :id="id"
          v-model="values.mediumTextInput"
          aria-label="medium text input"
          size="medium"
        />
      </InputField>
    </KitchensinkStory>

    <KitchensinkStory class="w-320" name="Small">
      <InputField
        id="smallTextInput"
        v-slot="{ id }"
        label="Regular text input"
      >
        <TextInput
          :id="id"
          v-model="values.smallTextInput"
          aria-label="small text input"
          size="small"
        />
      </InputField>
    </KitchensinkStory>

    <KitchensinkStory class="w-320" name="Date, year, time">
      <InputField
        id="dateInput"
        v-slot="{ id, hasError, updateValidationError }"
        label="Date input"
      >
        <DateInput
          :id="id"
          v-model="values.dateInput"
          aria-label="Date input"
          :has-error="hasError"
          @update:validation-error="updateValidationError"
        />
      </InputField>

      <InputField
        id="yearInput"
        v-slot="{ id, hasError, updateValidationError }"
        label="Year input"
      >
        <YearInput
          :id="id"
          v-model="values.yearInput"
          :has-error="hasError"
          @update:validation-error="updateValidationError"
        />
      </InputField>
      <div class="mb-12">
        <span class="ds-label-03-bold mr-8">Current year value:</span>
        <span class="ds-label-03-reg">{{ values.yearInput }}</span>
      </div>

      <InputField id="timeInput" v-slot="{ id }" label="Time input">
        <TimeInput :id="id" v-model="values.timeInput" />
      </InputField>
    </KitchensinkStory>

    <KitchensinkStory class="w-320" name="Nested">
      <NestedInput
        aria-label="Nested Input"
        :fields="nestedInputFields"
      ></NestedInput>
    </KitchensinkStory>
  </KitchensinkPage>
</template>
