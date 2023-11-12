import './shared/ArrayExtensions'
import sinon from 'sinon'
import sinonChai from 'sinon-chai'
import chai from 'chai'

chai.use(sinonChai)
global.fetch = window.fetch = () => new Promise(() => {})

afterEach(() => {
  sinon.restore()
  vi.restoreAllMocks()
})
