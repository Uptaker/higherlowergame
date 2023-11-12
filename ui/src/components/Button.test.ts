import {render} from '@testing-library/svelte'
import Button from './Button.svelte'

it('can assign button type', () => {
  const {container} = render(Button, {type: 'submit', label: 'Title'})
  const button = container.querySelector('button')!
  expect(button.getAttribute('type')).to.eq('submit')
  expect(button.textContent).to.contain('Title')
})

it('button type is `button` by default', () => {
  const {container} = render(Button)
  expect(container.querySelector('button')!.getAttribute('type')).to.eq('button')
})
