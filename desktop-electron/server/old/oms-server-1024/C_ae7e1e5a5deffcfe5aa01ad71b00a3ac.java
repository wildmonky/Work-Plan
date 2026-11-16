import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_ae7e1e5a5deffcfe5aa01ad71b00a3ac
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        return "<span datavalue='"+mapRow.get("DEAD_TIMESET_EX")+"' onclick='pw_notify_deadnumline_view(this);'>"+mapRow.get("DEAD_TIMESET_EX")+"</span>";

    }
}
