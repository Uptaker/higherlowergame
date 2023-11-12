import {render} from '@testing-library/svelte'
import NumberField from './NumberField.svelte'

it('renders', () => {
  const {container} = render(NumberField, {label: 'Number', unit: '€'})
  const input = container.querySelector('input') as HTMLInputElement
  expect(input.id).to.eq('Number')
  expect(input.type).to.eq('number')
  expect(input.required).to.be.true
  expect(input.maxLength).to.eq(10)
  expect(container.querySelector('label')!.textContent!.trim()).to.eq('Number')
  expect(container.querySelector('.unit')!.textContent).to.eq('€')
})
