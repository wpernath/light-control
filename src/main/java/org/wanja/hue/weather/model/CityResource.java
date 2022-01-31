package org.wanja.hue.weather.model;

public class CityResource {
    public String city;
    public String zip;
    public String country;

    public CityResource() {
    }

    public CityResource(String city, String zip, String country) {
        this.city = city;
        this.country = country;
        this.zip = zip;
    }

    public String queryString() {
        return new StringBuilder()
            .append(city)
            .append(",")
            .append(zip)
            .append(",")
            .append(country)
            .toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((city == null) ? 0 : city.hashCode());
        result = prime * result + ((country == null) ? 0 : country.hashCode());
        result = prime * result + ((zip == null) ? 0 : zip.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CityResource other = (CityResource) obj;
        if (city == null) {
            if (other.city != null)
                return false;
        } else if (!city.equals(other.city))
            return false;
        if (country == null) {
            if (other.country != null)
                return false;
        } else if (!country.equals(other.country))
            return false;
        if (zip == null) {
            if (other.zip != null)
                return false;
        } else if (!zip.equals(other.zip))
            return false;
        return true;
    }

    
}
