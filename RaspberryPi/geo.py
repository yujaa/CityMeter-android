
import Geoclue

providers = 'Yahoo'
providers = providers.split(', ')
for provider in providers:

  POS_PROVIDER = provider

  location = Geoclue.DiscoverLocation()
  location.init()
  location.set_position_provider(POS_PROVIDER)
  position = location.get_location_info()

  print (provider)
  print (position['latitude'])
  print (position['longitude'])
