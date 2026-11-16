import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_d219df06124398e1c8ea372b761de881
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        return "<span datavalue='"+mapRow.get("NOTICE_CONTENT")+"' onclick='pw_notify_deadnumline_view(this);'>"+mapRow.get("NOTICE_CONTENT")+"</span>";

    }
}
