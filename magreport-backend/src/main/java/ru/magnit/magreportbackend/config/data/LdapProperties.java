package ru.magnit.magreportbackend.config.data;


import lombok.Getter;
import lombok.Setter;
import ru.magnit.magreportbackend.service.enums.LdapTypes;

@Getter
@Setter
public class LdapProperties {
    private LdapTypes type;
    private String description;
    private String url;
    private String domainName;
    private String base;
    private String groupPath;
    private String userBase;
    private String userFilter;
    private String userSearchFilter;
    private Long batchSize;
    private String userDn;
    private String password;
    private String loginParamName;
    private String mailParamName;
    private String fullNameParamName;

    public String[] getGroupPaths() {
        return groupPath.split(";");
    }

    public String[] getUserPaths() {
        return userBase.split(";");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LdapProperties that = (LdapProperties) o;

        if (getType() != that.getType()) return false;
        if (getDescription() != null ? !getDescription().equals(that.getDescription()) : that.getDescription() != null)
            return false;
        if (getUrl() != null ? !getUrl().equals(that.getUrl()) : that.getUrl() != null) return false;
        if (getBase() != null ? !getBase().equals(that.getBase()) : that.getBase() != null) return false;
        if (getGroupPath() != null ? !getGroupPath().equals(that.getGroupPath()) : that.getGroupPath() != null)
            return false;
        if (getUserBase() != null ? !getUserBase().equals(that.getUserBase()) : that.getUserBase() != null)
            return false;
        if (getUserFilter() != null ? !getUserFilter().equals(that.getUserFilter()) : that.getUserFilter() != null)
            return false;
        if (getUserSearchFilter() != null ? !getUserSearchFilter().equals(that.getUserSearchFilter()) : that.getUserSearchFilter() != null)
            return false;
        return getBatchSize() != null ? getBatchSize().equals(that.getBatchSize()) : that.getBatchSize() == null;
    }

    @Override
    public int hashCode() {
        int result = getType() != null ? getType().hashCode() : 0;
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + (getUrl() != null ? getUrl().hashCode() : 0);
        result = 31 * result + (getBase() != null ? getBase().hashCode() : 0);
        result = 31 * result + (getGroupPath() != null ? getGroupPath().hashCode() : 0);
        result = 31 * result + (getUserBase() != null ? getUserBase().hashCode() : 0);
        result = 31 * result + (getUserFilter() != null ? getUserFilter().hashCode() : 0);
        result = 31 * result + (getUserSearchFilter() != null ? getUserSearchFilter().hashCode() : 0);
        result = 31 * result + (getBatchSize() != null ? getBatchSize().hashCode() : 0);
        return result;
    }
}
