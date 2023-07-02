package br.com.gpds.domain.common;

public final class DomainConstants {

    public static final String API_PATH = "/api";
    public static final String APP_PATH = API_PATH + "/gpds";
    public static final String AUTH_PATH = API_PATH + "/authenticate";
    public static final String CUSTOMERS = "/clientes/";
    public static final String CUSTOMER = "/cliente/";
    public static final String PROJECTS = "/projetos/";
    public static final String PROJECT = "/projeto/";
    public static final String TEAMS = "/times-de-devsecops/";
    public static final String STATUSES = "/estados/";
    public static final String ACTIVITIES = "/atividades/";
    public static final String ACTIVITY = "/atividade/";
    public static final String SERVICE_LETTER_OPENING = "Celebração de Contrato";
    public static final long STARTED_STATUS_ID = 5L;
    public static final long ON_CREATE_STATUS_ID = 1L;

    private DomainConstants() {
    }
}
