package it.sintegra.badge.business.entity;

public class User {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column mys_user.id
     *
     * @mbggenerated Wed Giu 22 16:15:50 CEST 2016
     */
    private Short id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column mys_user.code
     *
     * @mbggenerated Wed Giu 22 16:15:50 CEST 2016
     */
    private Short code;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column mys_user.firstname
     *
     * @mbggenerated Wed Giu 22 16:15:50 CEST 2016
     */
    private String firstname;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column mys_user.lastname
     *
     * @mbggenerated Wed Giu 22 16:15:50 CEST 2016
     */
    private String lastname;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column mys_user.username
     *
     * @mbggenerated Wed Giu 22 16:15:50 CEST 2016
     */
    private String username;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column mys_user.password
     *
     * @mbggenerated Wed Giu 22 16:15:50 CEST 2016
     */
    private String password;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column mys_user.profile
     *
     * @mbggenerated Wed Giu 22 16:15:50 CEST 2016
     */
    private String profile;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column mys_user.qualify
     *
     * @mbggenerated Wed Giu 22 16:15:50 CEST 2016
     */
    private String qualify;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column mys_user.id
     *
     * @return the value of mys_user.id
     *
     * @mbggenerated Wed Giu 22 16:15:50 CEST 2016
     */
    public Short getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column mys_user.id
     *
     * @param id the value for mys_user.id
     *
     * @mbggenerated Wed Giu 22 16:15:50 CEST 2016
     */
    public void setId(Short id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column mys_user.code
     *
     * @return the value of mys_user.code
     *
     * @mbggenerated Wed Giu 22 16:15:50 CEST 2016
     */
    public Short getCode() {
        return code;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column mys_user.code
     *
     * @param code the value for mys_user.code
     *
     * @mbggenerated Wed Giu 22 16:15:50 CEST 2016
     */
    public void setCode(Short code) {
        this.code = code;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column mys_user.firstname
     *
     * @return the value of mys_user.firstname
     *
     * @mbggenerated Wed Giu 22 16:15:50 CEST 2016
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column mys_user.firstname
     *
     * @param firstname the value for mys_user.firstname
     *
     * @mbggenerated Wed Giu 22 16:15:50 CEST 2016
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname == null ? null : firstname.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column mys_user.lastname
     *
     * @return the value of mys_user.lastname
     *
     * @mbggenerated Wed Giu 22 16:15:50 CEST 2016
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column mys_user.lastname
     *
     * @param lastname the value for mys_user.lastname
     *
     * @mbggenerated Wed Giu 22 16:15:50 CEST 2016
     */
    public void setLastname(String lastname) {
        this.lastname = lastname == null ? null : lastname.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column mys_user.username
     *
     * @return the value of mys_user.username
     *
     * @mbggenerated Wed Giu 22 16:15:50 CEST 2016
     */
    public String getUsername() {
        return username;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column mys_user.username
     *
     * @param username the value for mys_user.username
     *
     * @mbggenerated Wed Giu 22 16:15:50 CEST 2016
     */
    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column mys_user.password
     *
     * @return the value of mys_user.password
     *
     * @mbggenerated Wed Giu 22 16:15:50 CEST 2016
     */
    public String getPassword() {
        return password;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column mys_user.password
     *
     * @param password the value for mys_user.password
     *
     * @mbggenerated Wed Giu 22 16:15:50 CEST 2016
     */
    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column mys_user.profile
     *
     * @return the value of mys_user.profile
     *
     * @mbggenerated Wed Giu 22 16:15:50 CEST 2016
     */
    public String getProfile() {
        return profile;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column mys_user.profile
     *
     * @param profile the value for mys_user.profile
     *
     * @mbggenerated Wed Giu 22 16:15:50 CEST 2016
     */
    public void setProfile(String profile) {
        this.profile = profile == null ? null : profile.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column mys_user.qualify
     *
     * @return the value of mys_user.qualify
     *
     * @mbggenerated Wed Giu 22 16:15:50 CEST 2016
     */
    public String getQualify() {
        return qualify;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column mys_user.qualify
     *
     * @param qualify the value for mys_user.qualify
     *
     * @mbggenerated Wed Giu 22 16:15:50 CEST 2016
     */
    public void setQualify(String qualify) {
        this.qualify = qualify == null ? null : qualify.trim();
    }
}