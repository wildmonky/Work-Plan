import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_844f4d069d5d3e29d8a90324654f224e
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        return "<span datavalue='"+mapRow.get("DEAD_TIMESET_EX")+"' onclick='pw_deadnumline_editpoint(this);'>"+mapRow.get("DEAD_TIMESET_EX")+"</span>";

    }
}
