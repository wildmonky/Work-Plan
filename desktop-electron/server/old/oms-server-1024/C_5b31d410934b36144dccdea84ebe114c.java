import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_5b31d410934b36144dccdea84ebe114c
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        return mapRow.get("DEAD_TIMESET_EX").toString().isEmpty()?"":"<span datavalue='"+mapRow.get("DEAD_TIMESET_EX")+"' onclick='pw_notify_deadnumline_view(this);'>查看</span>";

    }
}
