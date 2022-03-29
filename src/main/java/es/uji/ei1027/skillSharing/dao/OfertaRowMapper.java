package es.uji.ei1027.skillSharing.dao;

import es.uji.ei1027.skillSharing.modelo.Oferta;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class OfertaRowMapper implements RowMapper<Oferta> {
    public Oferta mapRow(ResultSet rs, int rowNum)throws SQLException{
        Oferta oferta=new Oferta();
        oferta.setIdOferta(rs.getInt("id_oferta"));
        oferta.setEstudiante(rs.getString("estudiante"));
        oferta.setHoras(rs.getInt("horas"));
        oferta.setIniFecha(rs.getObject("ini_fecha", LocalDate.class));
        oferta.setFinFecha(rs.getObject("fin_fecha", LocalDate.class));
        oferta.setSkill(rs.getInt("id_skill"));
        oferta.setActiva(rs.getBoolean("activa"));
        oferta.setDescripcion(rs.getString("descripcion"));
        return oferta;
    }
}
